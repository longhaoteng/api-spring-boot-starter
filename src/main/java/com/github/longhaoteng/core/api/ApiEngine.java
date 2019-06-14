package com.github.longhaoteng.core.api;


import com.github.longhaoteng.core.common.AccessToken;
import com.github.longhaoteng.core.common.AccessTokenManager;
import com.github.longhaoteng.core.exception.ApiException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * api engine
 *
 * @author mr.long
 */
@Slf4j
public class ApiEngine {

    // access token manager
    private AccessTokenManager accessTokenManager;

    // ApiHandler mapping
    private Map<String, ApiHandler> mapping;

    public ApiEngine(AccessTokenManager accessTokenManager, List<ApiHandler> handlers) {
        this.accessTokenManager = accessTokenManager;
        mapping = new HashMap<>();
        if (handlers != null) {
            for (ApiHandler handler : handlers) {
                API api = handler.getClass().getAnnotation(API.class);
                if (api == null) {
                    continue;
                }
                log.info("API {} loaded", api.value());
                mapping.put(api.value(), handler);
            }
        }
    }

    /**
     * handle request
     *
     * @param request api request
     * @return response
     */
    public Response handle(Request request) {
        ApiHandler handler = mapping.get(request.getService());
        if (handler == null) {
            return Response.builder().code(HttpStatus.NOT_FOUND.value()).message("API not found.").build();
        }
        try {
            String token = request.getParameter("access_token");
            API api = handler.getClass().getAnnotation(API.class);
            AccessToken accessToken = null;
            // need login
            if (api.needLogin()) {
                if (token == null) {
                    // 未登录
                    return Response.builder().code(HttpStatus.UNAUTHORIZED.value()).message("Not logged in.").build();
                }
                // 管理员
                if (api.admin()) {
                    token = "admin." + token;
                } else {
                    token = "user." + token;
                }
                accessToken = accessTokenManager.find(token);
                if (accessToken == null) {
                    // 登录过期
                    return Response.builder().code(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value()).message(
                            "Login expires.").build();
                } else {
                    // 重置token过期时间
                    accessTokenManager.setExpireTime(token, 7200L);
                }
            }
            Response response = Response.builder().build();
            Map<String, Object> resp = Maps.newHashMap();
            handler.handle(request, response, resp, accessToken);
            if (response.getCode() == null) {
                response.setCode(HttpStatus.OK.value()).setMessage("Success.");
            }
            return response.setData(resp);
        } catch (ApiException e) {
            return Response.builder().code(e.getCode()).message(e.getMessage()).build();
        } catch (Exception e) {
            log.error("handle", e);
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(e.getMessage()).build();
        }
    }
}
