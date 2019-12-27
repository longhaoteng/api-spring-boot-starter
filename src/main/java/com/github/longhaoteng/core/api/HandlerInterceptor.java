package com.github.longhaoteng.core.api;

import com.github.longhaoteng.core.common.AccessToken;

import java.util.Map;

/**
 * handler interceptor
 *
 * @author mr.long
 */
public interface HandlerInterceptor {

    /**
     * Intercept the execution of a handler
     * Executed before ApiHandler
     *
     * @param request 请求参数
     * @throws Exception in case of errors
     */
    default boolean preHandle(Request request) throws Exception {
        return true;
    }

    /**
     * Intercept the execution of a handler
     * Executed after ApiHandler
     *
     * @param request     请求参数
     * @param response    响应
     * @param resp        响应参数
     * @param accessToken token
     * @throws Exception in case of errors
     */
    default void postHandle(Request request, Response response, Map<String, Object> resp, AccessToken accessToken) throws Exception {}

    /**
     * Callback after completion of request processing
     *
     * @param request     请求参数
     * @param response    响应
     * @param resp        响应参数
     * @param accessToken token
     * @throws Exception in case of errors
     */
    default void afterCompletion(Request request, Response response, Map<String, Object> resp,
                                 AccessToken accessToken) throws Exception {}
}
