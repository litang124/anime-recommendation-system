package com.anime.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A/B 测试配置服务
 * 使用 Redis 存储实验配置，支持动态更新
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AbTestConfigService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CONFIG_KEY = "ab:test:config";

    /**
     * 获取当前实验配置
     */
    public Map<String, Object> getConfig() {
        try {
            String json = redisTemplate.opsForValue().get(CONFIG_KEY);
            if (json != null && !json.trim().isEmpty()) {
                return objectMapper.readValue(json, HashMap.class);
            }
        } catch (JsonProcessingException e) {
            log.error("解析 A/B 配置失败", e);
        }
        // 默认配置：全量走策略 A
        Map<String, Object> defaultConfig = new HashMap<>();
        defaultConfig.put("enabled", true);
        defaultConfig.put("experimentName", "hybrid-v1-default");
        defaultConfig.put("strategies", Map.of(
                "A", Map.of("weight", 100, "className", "HybridRecommendService"),
                "B", Map.of("weight", 0, "className", "UserProfileWeightedStrategy")
        ));
        return defaultConfig;
    }

    /**
     * 更新实验配置
     */
    public void updateConfig(Map<String, Object> config) {
        try {
            String json = objectMapper.writeValueAsString(config);
            redisTemplate.opsForValue().set(CONFIG_KEY, json, 1, TimeUnit.HOURS);
            log.info("A/B 配置已更新：{}", config.getOrDefault("experimentName", "unknown"));
        } catch (JsonProcessingException e) {
            log.error("序列化 A/B 配置失败", e);
        }
    }

    /**
     * 获取用户所属策略（基于 userId 分桶）
     */
    public String getStrategyForUser(Long userId) {
        if (userId == null) {
            return "A";
        }
        Map<String, Object> config = getConfig();
        if (!(Boolean) config.getOrDefault("enabled", false)) {
            return "A";
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> strategies = (Map<String, Object>) config.getOrDefault("strategies", new HashMap<>());
        if (strategies.isEmpty()) {
            return "A";
        }

        // 简单哈希分桶：userId % 100 → [0,99]
        int bucket = Math.abs(userId.hashCode()) % 100;
        int cumulative = 0;
        for (Map.Entry<String, Object> entry : strategies.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> strategyConf = (Map<String, Object>) entry.getValue();
            Integer weight = (Integer) strategyConf.getOrDefault("weight", 0);
            if (weight == null) weight = 0;
            cumulative += weight;
            if (bucket < cumulative) {
                return entry.getKey();
            }
        }
        return "A"; // fallback
    }
}