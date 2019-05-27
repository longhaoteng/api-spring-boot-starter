package com.github.longhaoteng.core.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * request
 *
 * @author mr.long
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Request {

    // 服务名
    private String service;

    // 请求参数
    private Map<String, Object> params;

    // 请求头
    private HttpServletRequest servlet;

    /**
     * 获取参数
     *
     * @param name 参数名
     * @param <T>  值类型
     * @return 参数值
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name) {
        return (T) params.get(name);
    }
}
