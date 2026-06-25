package com.anime.service;

import java.util.List;
import java.util.Map;

/**
 * 用户画像分析服务
 */
public interface UserAnalysisService {
    
    /**
     * 获取用户画像概览
     * @param userId 用户 ID
     * @return 用户画像数据
     */
    Map<String, Object> getUserProfile(Long userId);
    
    /**
     * 获取用户观看统计
     * @param userId 用户 ID
     * @return 观看统计数据
     */
    Map<String, Object> getWatchStatistics(Long userId);
    
    /**
     * 获取用户偏好标签（基于观看历史和评分）
     * @param userId 用户 ID
     * @return 偏好标签列表
     */
    List<Map<String, Object>> getUserPreferences(Long userId);
    
    /**
     * 获取用户评分分布
     * @param userId 用户 ID
     * @return 评分分布数据
     */
    Map<String, Object> getRatingDistribution(Long userId);
    
    /**
     * 获取用户活跃时间段
     * @param userId 用户 ID
     * @return 活跃时间段数据
     */
    Map<String, Object> getActiveTimeSlots(Long userId);
}
