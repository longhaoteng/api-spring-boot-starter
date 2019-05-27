package com.github.longhaoteng.core.configuration;

import com.github.longhaoteng.core.api.ApiEngine;
import com.github.longhaoteng.core.api.ApiHandler;
import com.github.longhaoteng.core.api.Application;
import com.github.longhaoteng.core.common.AccessTokenManager;
import com.github.longhaoteng.core.common.CacheAccessTokenManager;
import com.github.longhaoteng.core.common.RedisHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * AutoConfiguration
 *
 * @author mr.long
 */
@Configuration
public class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Application api() {
        return new Application();
    }

    @Bean
    public RedisHelper redisHelper(@Qualifier("redisTemplate") RedisTemplate redis) {
        return new RedisHelper(redis);
    }

    @Bean
    public ApiEngine buildApiEngine(AccessTokenManager accessTokenManager, List<ApiHandler> handlers) {
        return new ApiEngine(accessTokenManager, handlers);
    }

    @Bean
    public AccessTokenManager buildAccessTokenManager() {
        return new CacheAccessTokenManager();
    }

}
