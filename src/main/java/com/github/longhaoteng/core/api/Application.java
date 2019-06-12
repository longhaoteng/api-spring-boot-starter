package com.github.longhaoteng.core.api;

import com.github.longhaoteng.core.common.ApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Application {

    @Autowired
    private ApiProperties properties;

    @Autowired
    private ApiEngine apiEngine;

    @PostMapping(value = "/api", consumes = "application/json", produces = "application/json")
    public Response api(HttpServletRequest httpServletRequest, @RequestBody Request request) {
        String auth = httpServletRequest.getHeader("auth");
        request.setServlet(httpServletRequest);
        if (StringUtils.isEmpty(properties.getAuth())) {
            return apiEngine.handle(request);
        } else {
            return properties.getAuth().equals(auth) ? apiEngine.handle(request) : null;
        }
    }
}
