package com.github.longhaoteng.core.common;

import java.util.UUID;

/**
 * Cache Access Token Manager
 *
 * @author mr.long
 */
public class CacheAccessTokenManager implements AccessTokenManager {

    /**
     * Find access token by key
     *
     * @param key access token key
     * @return access token
     */
    @Override
    public AccessToken find(String key) {
        return (AccessToken) RedisHelper.get(key);
    }

    /**
     * Save access token
     *
     * @param key         access token key
     * @param accessToken access token
     */
    @Override
    public void save(String key, AccessToken accessToken) {
        RedisHelper.set(key, accessToken);
    }

    /**
     * Create and save access token
     *
     * @param accessToken access token
     * @return access token key
     */
    @Override
    public String save(AccessToken accessToken) {
        String key = UUID.randomUUID().toString();
        RedisHelper.set(key, accessToken);
        return key;
    }

    /**
     * Save access token
     *
     * @param key         access token key
     * @param accessToken access token
     * @param expireTime  过期时间/单位s
     */
    @Override
    public void save(String key, AccessToken accessToken, Long expireTime) {
        RedisHelper.set(key, accessToken, expireTime);
    }

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param expireTime  过期时间/单位s
     */
    @Override
    public String save(AccessToken accessToken, Long expireTime) {
        String key = UUID.randomUUID().toString();
        RedisHelper.set(key, accessToken, expireTime);
        return key;
    }

    /**
     * Remove access token
     *
     * @param key access token key
     */
    @Override
    public void remove(String key) {
        RedisHelper.remove(key);
    }

    /**
     * 设置一个key的过期时间
     *
     * @param key        key
     * @param expireTime 过期时间/单位s
     */
    @Override
    public void setExpireTime(String key, Long expireTime) {
        RedisHelper.setExpireTime(key, expireTime);
    }
}
