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

    // user id
    private Object userId;

    // token
    private String token;

    // user
    private Object user;

    // role
    private String role;

    // 用户登录时环境md5值
    private String label;

}
