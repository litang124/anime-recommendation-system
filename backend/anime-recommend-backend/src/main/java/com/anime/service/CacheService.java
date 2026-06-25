package com.anime.service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务接口
 */
public interface CacheService {
    
    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    void set(String key, Object value, long timeout, TimeUnit unit);
    
    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    Object get(String key);
    
    /**
     * 删除缓存
     * @param key 键
     */
    void delete(String key);
    
    /**
     * 检查缓存是否存在
     * @param key 键
     * @return true/false
     */
    boolean hasKey(String key);
    
    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    void expire(String key, long timeout, TimeUnit unit);
}
