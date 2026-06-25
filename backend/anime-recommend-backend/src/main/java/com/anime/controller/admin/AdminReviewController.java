package com.anime.controller.admin;

import com.anime.annotation.AdminRequired;
import com.anime.entity.Review;
import com.anime.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论管理（后台）
 */
@RestController
@RequestMapping("/admin/review")
@RequiredArgsConstructor
@Slf4j
public class AdminReviewController {

    private final ReviewService reviewService;

    /**
     * 获取评论列表
     */
    @GetMapping("/list")
    @AdminRequired(operationType = "REVIEW_LIST", description = "查询评论列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long animeId,
            @RequestParam(required = false) Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews;
            
            if (animeId != null) {
                // 按动漫 ID 查询
                reviews = reviewService.findByAnimeId(animeId);
            } else if (userId != null) {
                // 按用户 ID 查询
                reviews = reviewService.findByUserId(userId);
            } else {
                // 获取全部评论（简化处理，实际应该分页）
                reviews = reviewService.findAll();
            }
            
            // 手动分页
            int start = page * size;
            int end = Math.min(start + size, reviews.size());
            List<Review> pageReviews = reviews.subList(start, end);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", pageReviews);
            response.put("total", reviews.size());
            response.put("pages", (int) Math.ceil((double) reviews.size() / size));
            response.put("currentPage", page);
            
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    @AdminRequired(operationType = "REVIEW_DELETE", description = "删除评论")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            reviewService.deleteById(id);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            log.error("删除评论失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 批量删除评论
     */
    @PostMapping("/batch-delete")
    @AdminRequired(operationType = "REVIEW_BATCH_DELETE", description = "批量删除评论")
    public Map<String, Object> batchDelete(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Long id : ids) {
                try {
                    reviewService.deleteById(id);
                    successCount++;
                } catch (Exception e) {
                    log.warn("删除评论失败：ID={}", id, e);
                    failCount++;
                }
            }
            
            response.put("code", 200);
            response.put("message", "批量删除完成");
            response.put("data", Map.of(
                    "total", ids.size(),
                    "success", successCount,
                    "fail", failCount
            ));
            
        } catch (Exception e) {
            log.error("批量删除失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取热门评论（点赞数高）
     */
    @GetMapping("/hot")
    @AdminRequired(operationType = "REVIEW_HOT", description = "获取热门评论")
    public Map<String, Object> getHotReviews(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> allReviews = reviewService.findAll();
            
            // 按点赞数排序
            List<Review> hotReviews = allReviews.stream()
                    .sorted((a, b) -> Integer.compare(
                            b.getLikeCount() != null ? b.getLikeCount() : 0,
                            a.getLikeCount() != null ? a.getLikeCount() : 0
                    ))
                    .limit(limit)
                    .toList();
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", hotReviews);
            response.put("count", hotReviews.size());
            
        } catch (Exception e) {
            log.error("获取热门评论失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 评论统计
     */
    @GetMapping("/statistics")
    @AdminRequired(operationType = "REVIEW_STATISTICS", description = "评论统计")
    public Map<String, Object> statistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> allReviews = reviewService.findAll();
            
            // 总评论数
            long totalCount = allReviews.size();
            
            // 平均评分
            double avgRating = allReviews.stream()
                    .filter(r -> r.getRating() != null)
                    .mapToDouble(r -> r.getRating())
                    .average()
                    .orElse(0.0);
            
            // 总点赞数
            int totalLikes = allReviews.stream()
                    .mapToInt(r -> r.getLikeCount() != null ? r.getLikeCount() : 0)
                    .sum();
            
            // 各评分段分布
            Map<String, Long> ratingDistribution = allReviews.stream()
                    .filter(r -> r.getRating() != null)
                    .collect(java.util.stream.Collectors.groupingBy(
                            r -> {
                                double rating = r.getRating();
                                if (rating >= 4.5) return "5 星";
                                if (rating >= 3.5) return "4 星";
                                if (rating >= 2.5) return "3 星";
                                if (rating >= 1.5) return "2 星";
                                return "1 星";
                            },
                            java.util.stream.Collectors.counting()
                    ));
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", Map.of(
                    "total", totalCount,
                    "avgRating", String.format("%.1f", avgRating),
                    "totalLikes", totalLikes,
                    "ratingDistribution", ratingDistribution
            ));
            
        } catch (Exception e) {
            log.error("获取评论统计失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }
}
