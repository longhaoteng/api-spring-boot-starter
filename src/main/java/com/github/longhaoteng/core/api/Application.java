package com.github.longhaoteng.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Application {

    @Value("${spring.api.auth}")
    private String auth;

    @Autowired
    private ApiEngine apiEngine;

    @PostMapping(value = "/api", consumes = "application/json", produces = "application/json")
    public Response api(HttpServletRequest httpServletRequest, @RequestBody Request request) {
        String auth = httpServletRequest.getHeader("auth");
        request.setServlet(httpServletRequest);
        if (StringUtils.isEmpty(this.auth)) {
            return apiEngine.handle(request);
        } else {
            return this.auth.equals(auth) ? apiEngine.handle(request) : null;
        }
    }
}
