package com.github.longhaoteng.core.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class Application {

    @Value("${api.auth}")
    private String auth;

    @Autowired
    private ApiEngine apiEngine;

    @PostMapping(value = "/api", consumes = "application/json", produces = "application/json")
    public Response api(HttpServletRequest httpServletRequest, @RequestBody Request request) {
        String auth = httpServletRequest.getHeader("auth");
        if (auth != null && auth.contains(auth)) {
            request.setServlet(httpServletRequest);
            return apiEngine.handle(request);
        }
        return null;
    }
}
