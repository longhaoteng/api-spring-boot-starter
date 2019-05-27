package com.github.longhaoteng.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * accessToken
 *
 * @author mr.long
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class AccessToken implements Serializable {

    // token
    private String token;

    // user
    private Object user;

    // 用户登录时环境md5值
    private String label;

}
