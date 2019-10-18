package com.github.longhaoteng.core.api;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * api
 *
 * @author mr.long
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Component
public @interface API {

    // service name
    String value();

    // 是否需要登录
    boolean needLogin() default false;

    // 角色
    String[] role() default {};

}
