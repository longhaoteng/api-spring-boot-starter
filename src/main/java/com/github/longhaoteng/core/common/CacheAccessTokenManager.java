package com.github.longhaoteng.core.common;

import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;

/**
 * Cache Access Token Manager
 *
 * @author mr.long
 */
public class CacheAccessTokenManager implements AccessTokenManager {

    private static final String SEPARATOR = ".";

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
     * Find access token by key
     *
     * @param key  access token key
     * @param role access token role
     * @return access token
     */
    @Override
    public AccessToken find(String key, String role) {
        return (AccessToken) RedisHelper.get(role + SEPARATOR + key);
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
        String key = DigestUtils.md5Hex(accessToken.toString() + LocalDateTime.now().toString());
        RedisHelper.set(key, accessToken);
        return key;
    }

    /**
     * Save access token
     *
     * @param key         access token key
     * @param accessToken access token
     * @param expireTime  expire time，unit s
     */
    @Override
    public void save(String key, AccessToken accessToken, Long expireTime) {
        RedisHelper.set(key, accessToken, expireTime);
    }

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param expireTime  expire time，unit s
     */
    @Override
    public String save(AccessToken accessToken, Long expireTime) {
        String key = DigestUtils.md5Hex(accessToken.toString() + LocalDateTime.now().toString());
        RedisHelper.set(key, accessToken, expireTime);
        return key;
    }

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param role        access token role
     */
    @Override
    public String save(AccessToken accessToken, String role) {
        String key = DigestUtils.md5Hex(accessToken.toString() + LocalDateTime.now().toString());
        RedisHelper.set(role + SEPARATOR + key, accessToken);
        return key;
    }

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param role        access token role
     * @param expireTime  expire time，unit s
     */
    @Override
    public String save(AccessToken accessToken, String role, Long expireTime) {
        String key = DigestUtils.md5Hex(accessToken.toString() + LocalDateTime.now().toString());
        RedisHelper.set(role + SEPARATOR + key, accessToken, expireTime);
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
     * Set the expiration time for a key
     *
     * @param key        key
     * @param expireTime expire time，unit s
     */
    @Override
    public void setExpireTime(String key, Long expireTime) {
        RedisHelper.setExpireTime(key, expireTime);
    }
}
