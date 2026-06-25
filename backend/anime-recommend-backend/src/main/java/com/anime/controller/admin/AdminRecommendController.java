package com.anime.controller.admin;

import com.anime.common.Result;
import com.anime.entity.Anime;
import com.anime.service.HybridRecommendService;
import com.anime.service.RecommendationService;
import com.anime.service.UserProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台推荐管理控制器
 */
@RestController
@RequestMapping("/admin/recommend")
@RequiredArgsConstructor
@Api(tags = "后台推荐管理")
public class AdminRecommendController {
    
    private final RecommendationService recommendationService;
    private final HybridRecommendService hybridRecommendService;
    private final UserProfileService userProfileService;
    
    @PostMapping("/user/{userId}")
    @ApiOperation("为指定用户生成推荐（混合推荐）")
    public Result<List<Anime>> generateRecommendationsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Anime> recommendations = recommendationService
                .getHybridRecommendationsForUser(userId, limit);
        
        return Result.success(recommendations);
    }
    
    @GetMapping("/user/{userId}/profile")
    @ApiOperation("获取用户画像")
    public Result getUserProfile(@PathVariable Long userId) {
        return Result.success(userProfileService.getUserProfile(userId));
    }
    
    @PostMapping("/user/{userId}/profile/update")
    @ApiOperation("手动更新用户画像")
    public Result updateUserProfile(@PathVariable Long userId) {
        userProfileService.updateUserProfile(userId);
        return Result.success("用户画像已更新");
    }
}
