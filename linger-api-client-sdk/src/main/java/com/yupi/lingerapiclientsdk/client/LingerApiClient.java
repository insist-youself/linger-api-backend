package com.yupi.lingerapiclientsdk.client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.yupi.lingerapiclientsdk.model.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.yupi.lingerapiclientsdk.utils.SignUtils.genSign;

/**
 * @author linger
 * @date 2024/2/12 23:02
 */
public class LingerApiClient {

    private String accessKey;

    private String secretKey;

    public static  String GATEWAY_HOST = "http://localhost:8090";
//    public static final String GATEWAY_HOST = "http://42.194.128.56:8090";

    public LingerApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public void setGatewayHost(String gatewayHost) {
        GATEWAY_HOST = gatewayHost;
    }

//    public String getNameByGet(String name) {
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("name", name);
//        String result= HttpUtil.get(GATEWAY_HOST + "/api/name/get", paramMap);
//        System.out.println(result);
//        return result;
//    }
//
//    public String getNameByPost(String name) {
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("name", name);
//        String result= HttpUtil.post(GATEWAY_HOST + "/api/name/post", paramMap);
//        System.out.println(result);
//        return result;
//    }

//    private Map<String, String> getHeaderMap(String body){
//        Map<String, String> hashMap = new HashMap<>();
//        // 注意：不能直接发送密钥
//        hashMap.put("accessKey", accessKey);
//        // 生成随机数（一个包含4个随机数的字符串）
//        hashMap.put("nonce", RandomUtil.randomNumbers(4));
//        // 请求内容
//        try {
//            body = URLEncoder.encode(body, "utf-8");
//            hashMap.put("body", body);
//            //hashMap.put("secretKey", secretKey);
//            hashMap.put("sign", genSign(body, secretKey));
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException("编码请求内容失败");
//        }
//        // 当前时间戳
//        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
//        return hashMap;
//    }

    private Map<String, String> getHeaderMap(String body, String method) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("nonce", RandomUtil.randomNumbers(10));
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", genSign(body, secretKey));
        body = URLUtil.encode(body, CharsetUtil.CHARSET_UTF_8);
        map.put("body", body);
        map.put("method", method);
        return map;
    }

//    public String getUserNameByPost(User user) {
//        String json = JSONUtil.toJsonStr(user);
//        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
//                .addHeaders(getHeaderMap(json))
//                .body(json)
//                .execute();
//        System.out.println(httpResponse.getStatus());
//        String result = httpResponse.body();
//        System.out.println(result);
//        return result;
//    }

    public String invokeInterface(String params, String url, String method) throws UnsupportedEncodingException {
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + url)
                .header("Accept-Charset", CharsetUtil.UTF_8)
                .addHeaders(getHeaderMap(params, method))
                .body(params)
                .execute();
        //formatJsonStr格式化JSON字符串，用于美化 JSON 字符串的输出
        return JSONUtil.formatJsonStr(httpResponse.body());
    }
}
