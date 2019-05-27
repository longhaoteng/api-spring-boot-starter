package com.github.longhaoteng.core.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

/**
 * response
 *
 * @author mr.long
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Response {

    private Integer code;

    private String message;

    private Object data;

    public Response(HttpStatus status, Object data) {
        this.code = status.value();
        this.data = data;
    }

    public Response(HttpStatus status, String message, Object data) {
        this.code = status.value();
        this.message = message;
        this.data = data;
    }
}
