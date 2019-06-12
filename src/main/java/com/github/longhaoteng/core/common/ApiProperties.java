package com.github.longhaoteng.core.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Api Properties
 *
 * @author mr.long
 */
@Data
@ConfigurationProperties(prefix = "spring.api")
public class ApiProperties {

    private String auth;

}
