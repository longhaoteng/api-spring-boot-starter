package com.github.longhaoteng.core.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.github.longhaoteng.core.exception.ApiException;
import org.springframework.http.HttpStatus;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

import static com.google.common.collect.Iterables.getFirst;

/**
 * base api
 *
 * @author mr.long
 */
public abstract class BaseApi implements ApiHandler {

    private static ObjectMapper mapper = new ObjectMapper();

    protected static <T> void validate(T object) throws ApiException {
        // 获得验证器
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        // 执行验证
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);
        // 如果有验证信息，则将第一个取出来包装成异常返回
        ConstraintViolation<T> constraintViolation = getFirst(constraintViolations, null);
        if (constraintViolation != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, String.valueOf(constraintViolation.getMessage()));
        }
    }

    protected static <T> T mapper(Request request, Class<T> c) throws ApiException {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(mapper.writeValueAsBytes(request.getParams()), c);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
