package com.anime.controller;

import com.anime.service.UserAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户画像分析控制器
 */
@RestController
@RequestMapping("/user/analysis")
@RequiredArgsConstructor
@Slf4j
public class UserAnalysisController {

    private final UserAnalysisService userAnalysisService;

    /**
     * 获取用户完整画像
     */
    @GetMapping("/profile/{userId}")
    public Map<String, Object> getUserProfile(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> profile = userAnalysisService.getUserProfile(userId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", profile);
            
        } catch (Exception e) {
            log.error("获取用户画像失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户观看统计
     */
    @GetMapping("/statistics/{userId}")
    public Map<String, Object> getStatistics(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = userAnalysisService.getWatchStatistics(userId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", stats);
            
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户偏好标签
     */
    @GetMapping("/preferences/{userId}")
    public Map<String, Object> getPreferences(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> preferences = userAnalysisService.getUserPreferences(userId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", preferences);
            response.put("count", preferences.size());
            
        } catch (Exception e) {
            log.error("获取偏好标签失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户评分分布
     */
    @GetMapping("/rating/{userId}")
    public Map<String, Object> getRatingDistribution(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> distribution = userAnalysisService.getRatingDistribution(userId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", distribution);
            
        } catch (Exception e) {
            log.error("获取评分分布失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户活跃时间
     */
    @GetMapping("/active-time/{userId}")
    public Map<String, Object> getActiveTime(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> activeTime = userAnalysisService.getActiveTimeSlots(userId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", activeTime);
            
        } catch (Exception e) {
            log.error("获取活跃时间失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }
}
