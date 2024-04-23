package com.yupi.lingerapigateway.filter;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.yupi.lingerapiclientsdk.utils.SignUtils;
import com.yupi.lingerapicommon.common.ErrorCode;
import com.yupi.lingerapicommon.model.entity.InterfaceInfo;
import com.yupi.lingerapicommon.model.entity.User;
import com.yupi.lingerapicommon.model.entity.UserInterfaceInfo;
import com.yupi.lingerapicommon.service.InnerInterfaceInfoService;
import com.yupi.lingerapicommon.service.InnerUserInterfaceInfoService;
import com.yupi.lingerapicommon.service.InnerUserService;
import com.yupi.lingerapigateway.exception.BusinessException;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

/**
 * @author linger
 */
@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");
    // 由于之前项目使用的是客户端sdk的单一api接口，此处保存了单一host,
    // 后续修改成适用于多接口的sdk需要从数据库中查询对应的host地址
//    public static final String INTERFACE_HOST = "http://localhost:8123";

    private static final String DYE_DATA_HEADER = "X-Dye-Data";

    private static final String DYE_DATA_VALUE = "linger";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 用户发送请求到api网关
        //2. 请求日志(包括请求唯一标识，请求路径、请求方法、参数、来源地址)
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识:" + request.getId());
        String path = request.getPath().value();
        log.info("请求路径:" + path);
        log.info("请求参数:" + request.getQueryParams());
        String IP_ADDRESS = request.getLocalAddress().getHostString();
        log.info("请求来源地址(Local):" + IP_ADDRESS);
        log.info("请求来源地址(Remote):" + request.getRemoteAddress());
        //3. 访问控制(黑白名单)
        // 3.1. 获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(IP_ADDRESS)) {
            return handleNoAuth(response);
        }
        //4. 用户鉴权处理（校验ak，sk是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = URLUtil.decode(headers.getFirst("body"), CharsetUtil.CHARSET_UTF_8);
        String method = headers.getFirst("method");

        if (StringUtil.isEmpty(nonce)
                || StringUtil.isEmpty(sign)
                || StringUtil.isEmpty(timestamp)
                || StringUtil.isEmpty(method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求头参数不完整！");
        }

        // 通过accessKey查询释放存在该用户
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if (invokeUser == null) {
            //如果用户信息为空，处理未授权信息并返回响应
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "accessKey 不合法！");
        }
        // 判断随机数是否存在，防止重放攻击
        String exitsNonce = (String)redisTemplate.opsForValue().get(nonce);
        if (StringUtil.isNotBlank(exitsNonce)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR,"请求重复！");
        }
        //if (Long.parseLong(nonce) > 10000L) {
        //    return handleNoAuth(response);
        //}
        // 若当前时间已超过存储时间 5 分钟，则判断失效
        long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if (currentTime - Long.parseLong(timestamp) >= FIVE_MINUTES) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求超时!");
        }

        // 实际情况是从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if (!sign.equals(serverSign)) {
            throw  new BusinessException(ErrorCode.FORBIDDEN_ERROR, "签名错误！");
        }
        //5. 请求的模拟接口是否存在？
        // 需要从数据库中查询模拟接口是否存在，以及请求方法是否匹配
        // 查询模拟接口是否存在
        InterfaceInfo invokeInterfaceInfo = null;
        try {
            invokeInterfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.info("getInterfaceInfo error", e);
        }
        if (invokeInterfaceInfo == null) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR, "接口不存在！");
        }
        /*// 判断接口调用次数是否 > 0
        Long invokeInterfaceInfoId = invokeInterfaceInfo.getId();
        Long userId = invokeUser.getId();
        Integer leftNum = innerUserInterfaceInfoService.getLeftNum(invokeInterfaceInfoId, userId);
        if(leftNum == null || leftNum <= 0) {
            return handleInvokeError(response);
        }*/
        //6. 请求转发，调用模拟接口
//        Mono<Void> filter = chain.filter(exchange);

        return handleResponse(exchange, chain, invokeInterfaceInfo.getId(), invokeUser.getId());
    }

    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();

            HttpStatus statusCode = originalResponse.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //7.调用 invokeCount,接口调用次数 + 1
                                // 因为网关项目没引入MyBatis等操作数据库的类库，因为该操作较为繁琐，可以由backend增删改查项目提供接口，我们直接调用，不用再重复写逻辑了。
                                try {
                                    postHandle(exchange.getRequest(), exchange.getResponse(), interfaceInfoId, userId);
                                } catch (Exception e) {
                                    log.info("invokeCount error", e);
                                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口调用次数 + 1 失败！");
                                }
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //data
                                String data = new String(content, StandardCharsets.UTF_8);
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果为: " + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 流量染色， 只有染色的数据才会被调用
                // exchange.getRequest().mutate()创建一个可变请求头，再添加自定义header
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header(DYE_DATA_HEADER, DYE_DATA_VALUE)
                        .build();
                // exchange.mutate() 创建一个可变交换对象，添加自定义的request和response
                ServerWebExchange serverWebExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .response(decoratedResponse)
                        .build();
                return chain.filter(serverWebExchange);
            }
            //降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 响应没权限
     *
     * @param response
     * @return
     */
    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.setRawStatusCode(HttpStatus.FORBIDDEN.value());
        return response.setComplete();
    }

    private void postHandle(ServerHttpRequest request, ServerHttpResponse response, Long interfaceInfoId, Long userId) {
        // 在API网关层加入Redission分布式锁，统一对接口调用次数 + 1操作做处理，避免高并发条件下出现线程安全问题，保证数据的一致性
        RLock lock = redissonClient.getLock("api:add_interface_num:" + userId);
        String nonce = request.getHeaders().getFirst("nonce");
        if (response.getStatusCode() == HttpStatus.OK) {
            CompletableFuture.runAsync(() -> {
                if (lock.tryLock()) {
                    try {
                        addInterfaceNum(nonce, interfaceInfoId, userId);
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }
    }

    private void addInterfaceNum(String nonce, Long interfaceInfoId, Long userId) {
        /*String nonce = request.getHeaders().getFirst("nonce");*/
        if (StringUtil.isEmpty(nonce)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "请求错误");
        }
        UserInterfaceInfo userInterfaceInfo = innerUserInterfaceInfoService.hasLeftNum(interfaceInfoId, userId);
        // 接口未绑定用户
        if (userInterfaceInfo == null) {
            Boolean save = innerUserInterfaceInfoService.addDefaultUserInterfaceInfo(interfaceInfoId, userId);
            if (save == null || !save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口绑定用户失败！");
            }
        }
        if (userInterfaceInfo != null && userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用次数已用完！");
        }
        redisTemplate.opsForValue().set(nonce, 1, 5, TimeUnit.MINUTES);
        innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}