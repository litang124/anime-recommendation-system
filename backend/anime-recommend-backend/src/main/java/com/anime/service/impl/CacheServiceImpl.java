package com.anime.service.impl;

import com.anime.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        log.debug("设置缓存：key={}", key);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public Object get(String key) {
        log.debug("获取缓存：key={}", key);
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        log.debug("删除缓存：key={}", key);
        redisTemplate.delete(key);
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        log.debug("设置过期时间：key={}, timeout={}, unit={}", key, timeout, unit);
        redisTemplate.expire(key, timeout, unit);
    }
}
