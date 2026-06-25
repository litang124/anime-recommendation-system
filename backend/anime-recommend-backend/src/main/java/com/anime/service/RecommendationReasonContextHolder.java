package com.anime.service;

import java.util.Map;

/**
 * 推荐理由上下文持有者（ThreadLocal）
 * 用于在推荐过程中传递算法层面的推荐理由
 */
public class RecommendationReasonContextHolder {
    
    private static final ThreadLocal<Map<Long, String>> REASON_CONTEXT = new ThreadLocal<>();
    
    /**
     * 设置推荐理由映射
     * @param reasons 动漫ID -> 推荐理由来源的映射
     */
    public static void setReasons(Map<Long, String> reasons) {
        REASON_CONTEXT.set(reasons);
    }
    
    /**
     * 获取推荐理由映射
     * @return 动漫ID -> 推荐理由来源的映射
     */
    public static Map<Long, String> getReasons() {
        return REASON_CONTEXT.get();
    }
    
    /**
     * 获取指定动漫的推荐理由
     * @param animeId 动漫ID
     * @return 推荐理由来源
     */
    public static String getReason(Long animeId) {
        Map<Long, String> reasons = REASON_CONTEXT.get();
        return reasons != null ? reasons.get(animeId) : null;
    }
    
    /**
     * 清除上下文（防止内存泄漏）
     */
    public static void clear() {
        REASON_CONTEXT.remove();
    }
}
