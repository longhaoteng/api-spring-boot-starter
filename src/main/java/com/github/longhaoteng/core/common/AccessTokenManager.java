package com.github.longhaoteng.core.common;

/**
 * access token manager
 *
 * @author mr.long
 */
public interface AccessTokenManager {

    /**
     * Find access token by key
     *
     * @param key access token key
     * @return access token
     */
    AccessToken find(String key);

    /**
     * Save access token
     *
     * @param key         access token key
     * @param accessToken access token
     */
    void save(String key, AccessToken accessToken);

    /**
     * Create and save access token
     *
     * @param accessToken access token
     * @return access token key
     */
    String save(AccessToken accessToken);

    /**
     * Save access token
     *
     * @param key         access token key
     * @param accessToken access token
     * @param expireTime  过期时间/单位s
     */
    void save(String key, AccessToken accessToken, Long expireTime);

    /**
     * Save access token
     *
     * @param accessToken access token
     * @param expireTime  过期时间/单位s
     * @return access token key
     */
    String save(AccessToken accessToken, Long expireTime);

    /**
     * Remove access token
     *
     * @param key access token key
     */
    void remove(String key);

    /**
     * 设置一个key的过期时间
     *
     * @param key        key
     * @param expireTime 过期时间/单位s
     */
    void setExpireTime(String key, Long expireTime);
}
