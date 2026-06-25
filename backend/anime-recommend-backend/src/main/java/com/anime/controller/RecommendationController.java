package com.anime.controller;

import com.anime.dto.MiniprogramAnimeDTO;
import com.anime.entity.Anime;
import com.anime.entity.Recommendation;
import com.anime.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐系统控制器
 */
@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;
    
    /**
     * 获取用户的个性化推荐
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        List<Anime> recommendations = recommendationService.getRecommendationsForUser(userId, limit);
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", recommendations);
        response.put("count", recommendations.size());
        
        return response;
    }
    
    /**
     * 获取推荐历史记录
     */
    @GetMapping("/history/{userId}")
    public Map<String, Object> getHistory(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<Recommendation> history = recommendationService.getRecommendationHistory(userId);
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", history);
        
        return response;
    }
    
    /**
     * 标记推荐为已点击
     */
    @PostMapping("/click/{recommendationId}")
    public Map<String, Object> markClicked(@PathVariable Long recommendationId) {
        Map<String, Object> response = new HashMap<>();
        recommendationService.markAsClicked(recommendationId);
        
        response.put("code", 200);
        response.put("message", "标记成功");
        
        return response;
    }
    
    /**
     * 获取用户的混合推荐（带推荐理由）
     */
    @GetMapping("/hybrid/user/{userId}")
    public Map<String, Object> getHybridRecommendationsWithReasons(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        List<MiniprogramAnimeDTO> recommendations = recommendationService
                .getHybridRecommendationsWithReasons(userId, limit);
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", recommendations);
        response.put("count", recommendations.size());
        
        return response;
    }
}
