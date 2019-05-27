package com.github.longhaoteng.core.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * ApiException
 *
 * @author mr.long
 */
@Getter
@Setter
@NoArgsConstructor
public class ApiException extends Exception {

    // error code
    private int code;

    // error message
    private String message;

    public ApiException(int code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
        this.message = message;
    }

}

