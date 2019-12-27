package com.github.longhaoteng.core.api;


import com.github.longhaoteng.core.common.AccessToken;
import com.github.longhaoteng.core.common.AccessTokenManager;
import com.github.longhaoteng.core.common.ApiProperties;
import com.github.longhaoteng.core.enums.ApiLoc;
import com.github.longhaoteng.core.exception.ApiException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ApiProperties properties;

    // access token manager
    private AccessTokenManager accessTokenManager;

    // ApiHandler mapping
    private Map<String, ApiHandler> mapping;

    @Autowired
    private HandlerInterceptor handlerInterceptor;

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
        try {
            if (handlerInterceptor.preHandle(request)) {
                ApiHandler handler =
                        properties.getLoc() != null && properties.getLoc().equals(ApiLoc.HEADER) ? mapping.get(
                                request.getServlet().getHeader("service")) : mapping.get(request.getService());
                if (handler == null) {
                    return Response.builder().code(HttpStatus.NOT_FOUND.value()).message("API not found.").build();
                }
                String token = request.getParameter("token");
                API api = handler.getClass().getAnnotation(API.class);
                AccessToken accessToken = null;
                // need login
                if (api.needLogin()) {
                    if (token == null) {
                        // not logged in
                        return Response.builder()
                                .code(HttpStatus.UNAUTHORIZED.value())
                                .message("Not logged in.")
                                .build();
                    }
                    // role
                    if (api.roles().length > 0) {
                        for (String role : api.roles()) {
                            accessToken = accessTokenManager.find(token, role);
                            if (accessToken != null) break;
                        }
                    } else {
                        accessToken = accessTokenManager.find(token);
                    }
                    if (accessToken == null) {
                        // login expires
                        return Response.builder()
                                .code(HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value())
                                .message("Login expires.")
                                .build();
                    }
                }
                Response response = Response.builder().build();
                Map<String, Object> resp = Maps.newHashMap();
                handlerInterceptor.postHandle(request, response, resp, accessToken);
                handler.handle(request, response, resp, accessToken);
                handlerInterceptor.afterCompletion(request, response, resp, accessToken);
                if (response.getCode() == null) {
                    response.setCode(HttpStatus.OK.value()).setMessage("Success.");
                }
                return response.setData(resp);
            }
            return Response.builder().code(HttpStatus.FORBIDDEN.value()).message("Forbidden.").build();
        } catch (ApiException e) {
            return Response.builder().code(e.getCode()).message(e.getMessage()).build();
        } catch (Exception e) {
            log.error("handle", e);
            return Response.builder().code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(e.getMessage()).build();
        }
    }
}
