package com.github.longhaoteng.core.api;

import com.github.longhaoteng.core.common.AccessToken;
import com.github.longhaoteng.core.exception.ApiException;

import java.util.Map;

/**
 * api handler
 *
 * @author mr.long
 */
public interface ApiHandler {

    /**
     * api请求
     *
     * @param request     请求参数
     * @param response    响应
     * @param resp        响应参数
     * @param accessToken token
     */
    void handle(Request request, Response response, Map<String, Object> resp, AccessToken accessToken) throws ApiException;
}
