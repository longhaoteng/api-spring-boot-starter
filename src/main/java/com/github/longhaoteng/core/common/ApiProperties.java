package com.github.longhaoteng.core.common;

import com.github.longhaoteng.core.enums.ApiLoc;
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

    // header auth字符串验证
    private String auth;

    // api value在request的位置
    private ApiLoc loc;

}
