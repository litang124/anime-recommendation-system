package com.anime.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序 Token 工具类
 * 基于 Redis 实现简单的 Token 管理
 */
@Component
@Slf4j
public class TokenUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Token 前缀
     */
    private static final String TOKEN_PREFIX = "token:";

    /**
     * Token 过期时间（7 天）
     */
    private static final long EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    /**
     * 生成 Token
     * @param openid 用户 openid
     * @return token
     */
    public String generateToken(String openid) {
        String token = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis();
        
        // 存入 Redis
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, openid, EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        log.info("生成 Token: {}, openid: {}", token, openid);
        return token;
    }

    /**
     * 验证 Token 并获取 openid
     * @param token token
     * @return openid，如果 token 无效返回 null
     */
    public String validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        String key = TOKEN_PREFIX + token;
        String openid = redisTemplate.opsForValue().get(key);
        
        if (openid != null) {
            // 续期（每次访问自动续期 7 天）
            redisTemplate.expire(key, EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
        
        return openid;
    }

    /**
     * 使 Token 失效（退出登录）
     * @param token token
     */
    public void invalidateToken(String token) {
        if (token != null && !token.isEmpty()) {
            String key = TOKEN_PREFIX + token;
            redisTemplate.delete(key);
            log.info("Token 已失效：{}", token);
        }
    }

    /**
     * 检查 Token 是否存在
     * @param token token
     * @return true/false
     */
    public boolean hasToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        String key = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
