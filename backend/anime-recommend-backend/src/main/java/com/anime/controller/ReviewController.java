package com.anime.controller;

import com.anime.entity.Review;
import com.anime.service.ReviewService;
import com.anime.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody Review review) {
        Map<String, Object> response = new HashMap<>();
        Review saved = reviewService.save(review);
        response.put("code", 200);
        response.put("message", "评论成功");
        response.put("data", saved);
        return response;
    }

    @GetMapping("/anime/{animeId}")
    public Map<String, Object> getByAnime(@PathVariable Long animeId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> reviews = reviewService.findByAnimeId(animeId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", reviews);
        return response;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> reviews = reviewService.findByUserId(userId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", reviews);
        return response;
    }

    @GetMapping("/anime/{animeId}/hot")
    public Map<String, Object> getHotByAnime(@PathVariable Long animeId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> reviews = reviewService.findHotByAnimeId(animeId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", reviews);
        return response;
    }

    @PutMapping("/{id}/like")
    public Map<String, Object> like(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Review review = reviewService.likeReview(id);
        if (review != null) {
            response.put("code", 200);
            response.put("message", "点赞成功");
            response.put("data", review);
        } else {
            response.put("code", 404);
            response.put("message", "评论不存在");
        }
        return response;
    }

    @PutMapping("/{id}/unlike")
    public Map<String, Object> unlike(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Review review = reviewService.unlikeReview(id);
        if (review != null) {
            response.put("code", 200);
            response.put("message", "取消点赞成功");
            response.put("data", review);
        } else {
            response.put("code", 404);
            response.put("message", "评论不存在");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        reviewService.deleteById(id);
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public Map<String, Object> reply(
            @RequestParam Long parentId,
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam(required = false) Double rating) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Review reply = reviewService.replyReview(parentId, userId, content, rating);
            
            response.put("code", 200);
            response.put("message", "回复成功");
            response.put("data", reply);
            
        } catch (RuntimeException e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "回复失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 查询某条评论的所有回复
     */
    @GetMapping("/replies/{parentId}")
    public Map<String, Object> getReplies(@PathVariable Long parentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> replies = reviewService.getReplies(parentId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", replies);
            response.put("count", replies.size());
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 查询动漫下的评论（包含回复）
     */
    @GetMapping("/anime/{animeId}/with-replies")
    public Map<String, Object> getCommentsWithReplies(@PathVariable Long animeId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> comments = reviewService.getCommentsWithReplies(animeId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", comments);
            response.put("count", comments.size());
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 用户评分（用于推荐算法反馈）
     * @param userId 用户ID
     * @param animeId 动漫ID
     * @param rating 评分（0-10）
     */
    @PostMapping("/rating")
    public Map<String, Object> rateAnime(
            @RequestParam Long userId,
            @RequestParam Long animeId,
            @RequestParam Double rating) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 创建评分评论
            Review review = new Review();
            review.setUserId(userId);
            review.setAnimeId(animeId);
            review.setContent("用户评分：" + rating + "分");
            review.setRating(rating);
            
            Review saved = reviewService.save(review);
            
            // 触发推荐算法更新
            recommendationService.updateRecommendationsForUser(userId);
            
            response.put("code", 200);
            response.put("message", "评分成功，推荐已更新");
            response.put("data", saved);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "评分失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取个性化社区推荐评论
     * 基于用户画像匹配相关热门评论
     * @param userId 用户ID
     * @param limit 返回数量
     */
    @GetMapping("/personalized/{userId}")
    public Map<String, Object> getPersonalizedCommunityReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.getPersonalizedCommunityReviews(userId, limit);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", reviews);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取个性化社区推荐失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取最新评论（全局，按时间倒序）
     * @param page 页码
     * @param size 每页数量
     */
    @GetMapping("/latest")
    public Map<String, Object> getLatestReviews(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.getLatestReviews(page, size);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", reviews);
            response.put("count", reviews.size());
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取最新评论失败：" + e.getMessage());
        }
        
        return response;
    }
}