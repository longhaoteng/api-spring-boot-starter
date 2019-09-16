package com.github.longhaoteng.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.longhaoteng.core.common.ApiProperties;
import com.github.longhaoteng.core.utils.AES;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Object api(HttpServletRequest httpServletRequest, @RequestBody String body) {
        String auth = httpServletRequest.getHeader("auth");
        if (StringUtils.isNotBlank(properties.getAuth()) && !properties.getAuth().equals(auth)) {
            return null;
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String key = properties.getKey();
            try {
                if (StringUtils.isNotBlank(key)) {
                    body = AES.decrypt(key, body);
                }
                Request request = mapper.readValue(body, Request.class);
                request.setServlet(httpServletRequest);
                Response response = apiEngine.handle(request);
                return StringUtils.isBlank(key) ? response : AES.encrypt(key, mapper.writeValueAsString(response));
            } catch (Exception e) {
                return null;
            }
        }
    }
}
