package com.yupi.lingerinterface.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author linger
 * @date 2024/3/4 16:36
 */

/**
 * 流量染色拦截器
 */
public class DyeDataInterceptor implements HandlerInterceptor {

    private static final String DYE_DATA_HEADER = "X-Dye-Data";
    private static final String DYE_DATA_VALUE = "linger";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的染色数据
        String header = request.getHeader(DYE_DATA_HEADER);
        // 判断对应的染色数据是否为空 或者 是否正确
        if(header == null || header != DYE_DATA_VALUE) {
            // 返回错误信息403
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
