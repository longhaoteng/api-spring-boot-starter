package com.github.longhaoteng.core.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

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
        String token = getToken(key, accessToken);
        RedisHelper.set(token, accessToken.setToken(token));
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
        String token = getToken(key, accessToken);
        RedisHelper.set(token, accessToken.setToken(token));
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
        String token = getToken(key, accessToken);
        RedisHelper.set(token, accessToken.setToken(token), expireTime);
    }

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param expireTime  expire time，unit s
     * @return access token key
     */
    @Override
    public String save(AccessToken accessToken, Long expireTime) {
        String key = DigestUtils.md5Hex(accessToken.toString() + LocalDateTime.now().toString());
        String token = getToken(key, accessToken);
        RedisHelper.set(token, accessToken.setToken(token), expireTime);
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

    /**
     * Get token
     *
     * @param key         key
     * @param accessToken access token
     * @return
     */
    private String getToken(String key, AccessToken accessToken) {
        accessToken.setKey(key);
        if (StringUtils.isBlank(accessToken.getRole())) {
            return key;
        } else {
            return accessToken.getRole() + SEPARATOR + key;
        }
    }
}
