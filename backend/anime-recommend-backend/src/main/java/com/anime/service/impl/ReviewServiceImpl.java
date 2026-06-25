package com.anime.service.impl;

import com.anime.entity.Anime;
import com.anime.entity.Review;
import com.anime.repository.AnimeRepository;
import com.anime.repository.ReviewRepository;
import com.anime.repository.UserProfileRepository;
import com.anime.service.AnimeService;
import com.anime.service.HybridRecommendService;
import com.anime.service.ReviewService;
import com.anime.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AnimeRepository animeRepository;
    private final AnimeService animeService;
    private final UserProfileService userProfileService;
    private final UserProfileRepository userProfileRepository;
    @Autowired
    private HybridRecommendService hybridRecommendService;

    @Override
    @Transactional
    public Review save(Review review) {
        if (review.getCreateTime() == null) {
            review.setCreateTime(LocalDateTime.now());
        }
        Review savedReview = reviewRepository.save(review);
        
        // 更新动漫评分和热度
        updateAnimeScoreAndHeat(review.getAnimeId());
        
        // 更新用户画像（用户评分行为影响偏好标签）
        if (review.getUserId() != null) {
            updateUserProfileAsync(review.getUserId());
        }
        
        return savedReview;
    }

    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Override
    public List<Review> findByAnimeId(Long animeId) {
        return reviewRepository.findByAnimeId(animeId);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> findHotByAnimeId(Long animeId) {
        return reviewRepository.findByAnimeIdOrderByLikeCountDesc(animeId);
    }

    @Override
    public List<Review> findNewByAnimeId(Long animeId) {
        return reviewRepository.findByAnimeIdOrderByCreateTimeDesc(animeId);
    }

    @Override
    @Transactional
    public Review likeReview(Long reviewId) {
        Review review = findById(reviewId);
        if (review != null) {
            review.setLikeCount(review.getLikeCount() + 1);
            Review savedReview = reviewRepository.save(review);
            
            // 更新动漫热度
            updateAnimeScoreAndHeat(review.getAnimeId());
            
            // 更新点赞用户的画像（通过评论ID获取点赞用户，暂时更新评论作者）
            // TODO: 后续可以添加点赞记录表来跟踪谁点赞了
            if (review.getUserId() != null) {
                updateUserProfileAsync(review.getUserId());
            }
            
            return savedReview;
        }
        return null;
    }

    @Override
    @Transactional
    public Review unlikeReview(Long reviewId) {
        Review review = findById(reviewId);
        if (review != null && review.getLikeCount() > 0) {
            review.setLikeCount(review.getLikeCount() - 1);
            Review savedReview = reviewRepository.save(review);
            
            // 更新动漫热度
            updateAnimeScoreAndHeat(review.getAnimeId());
            
            // 更新评论作者的画像
            if (review.getUserId() != null) {
                updateUserProfileAsync(review.getUserId());
            }
            
            return savedReview;
        }
        return null;
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional
    public Review replyReview(Long parentId, Long userId, String content, Double rating) {
        // 1. 查找父评论
        Review parentReview = findById(parentId);
        if (parentReview == null) {
            throw new RuntimeException("父评论不存在");
        }
        
        // 2. 创建回复
        Review reply = new Review();
        reply.setParentId(parentId);           // 设置父评论 ID
        reply.setUserId(userId);               // 回复者 ID
        reply.setAnimeId(parentReview.getAnimeId()); // 同一条动漫
        reply.setContent(content);             // 回复内容
        reply.setRating(rating);               // 评分（可选）
        reply.setReplyUserId(parentReview.getUserId()); // 被回复的用户 ID
        reply.setCreateTime(LocalDateTime.now());
        
        return reviewRepository.save(reply);
    }

    @Override
    public List<Review> getReplies(Long parentId) {
        return reviewRepository.findByParentIdOrderByCreateTimeAsc(parentId);
    }

    @Override
    public List<Review> getCommentsWithReplies(Long animeId) {
        // 1. 查询所有一级评论（parentId 为 null）
        List<Review> comments = reviewRepository.findByAnimeIdAndParentIdIsNullOrderByCreateTimeDesc(animeId);
        
        // 2. 为每条评论加载回复
        for (Review comment : comments) {
            List<Review> replies = getReplies(comment.getId());
            // 这里可以通过额外字段或单独接口返回回复列表
        }
        
        return comments;
    }

    @Override
    public List<Review> getPersonalizedCommunityReviews(Long userId, Integer limit) {
        try {
            // 复用已有的用户画像推荐逻辑
            List<Anime> recommendedAnimes = hybridRecommendService.hybridRecommend(userId, limit);
            
            // 将 Anime 映射为 Review（每部动漫生成一条“推荐理由评论”）
            List<Review> reviews = new ArrayList<>();
            for (Anime anime : recommendedAnimes) {
                Review review = new Review();
                review.setAnimeId(anime.getId());
                review.setContent("【系统推荐】" + (anime.getDescription() != null ? anime.getDescription().substring(0, Math.min(50, anime.getDescription().length())) : "这是一部优质动漫"));
                review.setRating(anime.getScore() != null ? anime.getScore() : 0.0);
                review.setLikeCount(0); // 初始无点赞
                review.setCreateTime(LocalDateTime.now());
                review.setRecommendReason(anime.getRecommendReason()); // 关键：注入推荐理由
                
                // 构建简易用户对象（模拟社区作者）
                com.anime.entity.User user = new com.anime.entity.User();
                user.setId(-1L); // 系统推荐标识
                user.setNickname("推荐系统");
                user.setAvatarUrl("/images/default-avatar.png");
                review.setUser(user);
                
                // 构建 Anime 对象（用于前端渲染）
                com.anime.entity.Anime animeForFrontend = new com.anime.entity.Anime();
                animeForFrontend.setId(anime.getId());
                animeForFrontend.setTitle(anime.getTitle());
                animeForFrontend.setCoverUrl(anime.getCoverUrl());
                animeForFrontend.setScore(anime.getScore());
                review.setAnime(animeForFrontend);
                
                reviews.add(review);
            }
            return reviews;
        } catch (Exception e) {
            log.error("获取个性化社区推荐失败", e);
            // 备用：返回空列表，避免前端报错
            return new ArrayList<>();
        }
    }
    
    /**
     * 更新动漫评分和热度
     * @param animeId 动漫ID
     */
    private void updateAnimeScoreAndHeat(Long animeId) {
        try {
            Optional<Anime> animeOpt = animeRepository.findById(animeId);
            if (animeOpt.isEmpty()) {
                return;
            }
            
            Anime anime = animeOpt.get();
            
            // 1. 计算平均评分
            List<Review> reviews = reviewRepository.findByAnimeId(animeId);
            if (!reviews.isEmpty()) {
                double avgScore = reviews.stream()
                    .filter(r -> r.getRating() != null && r.getRating() > 0)
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
                
                // 转换为10分制（用户评分是5分制）
                double calculatedScore = avgScore * 2.0;
                
                // 使用加权平均：原评分占95%，新评分占5%
                // 更稳定的评分策略，评分变化更缓慢，保护已有评分的权威性
                double originalScore = anime.getScore() != null ? anime.getScore() : 0.0;
                double finalScore;
                
                if (originalScore > 0) {
                    // 如果已有评分，使用加权平均
                    finalScore = originalScore * 0.95 + calculatedScore * 0.05;
                    log.debug("评分计算(加权) - 原评分: {}, 计算评分: {}, 最终评分: {}", 
                        originalScore, calculatedScore, finalScore);
                } else {
                    // 如果是首次评分，直接使用计算评分
                    finalScore = calculatedScore;
                    log.debug("评分计算(首次) - 计算评分: {}", calculatedScore);
                }
                
                // 保留一位小数
                anime.setScore(Math.round(finalScore * 10) / 10.0);
            }
            
            // 2. 计算热度：基于原有热度 + 评论数 + 总点赞数
            // 保留原有的基础热度，再加上评论和点赞的增量
            long reviewCount = reviews.size();
            long totalLikes = reviews.stream()
                .mapToInt(Review::getLikeCount)
                .sum();
            
            // 热度公式：原有基础热度 + 评论数 * 10 + 总点赞数 * 5
            // 如果原有热度为null或0，则从零开始计算
            int baseHeat = anime.getHeat() != null ? anime.getHeat() : 0;
            int calculatedHeat = (int)(reviewCount * 10 + totalLikes * 5);
            
            // 取最大值，确保热度不会降低
            int heat = Math.max(baseHeat, calculatedHeat);
            anime.setHeat(heat);
            
            // 3. 保存更新
            animeRepository.save(anime);
            
            // 4. 清除热门动漫缓存，确保前端获取最新排序
            animeService.updateScore(animeId);
            
            log.info("更新动漫评分和热度 - ID: {}, 评分: {}, 评论数: {}, 总点赞: {}, 原热度: {}, 计算热度: {}, 最终热度: {}", 
                animeId, anime.getScore(), reviewCount, totalLikes, baseHeat, calculatedHeat, heat);
                
        } catch (Exception e) {
            log.error("更新动漫评分和热度失败 - animeId: {}", animeId, e);
        }
    }
    
    /**
     * 异步更新用户画像（避免阻塞主流程）
     * @param userId 用户ID
     */
    private void updateUserProfileAsync(Long userId) {
        try {
            // 检查用户是否有画像记录
            if (userProfileRepository.existsById(userId)) {
                // 触发用户画像更新
                userProfileService.updateUserProfile(userId);
                log.info("触发用户画像更新 - userId: {}", userId);
            }
        } catch (Exception e) {
            // 用户画像更新失败不影响主流程
            log.warn("更新用户画像失败 - userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public List<Review> getLatestReviews(Integer page, Integer size) {
        // 获取所有评论，按创建时间倒序排序
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return reviewRepository.findAll(pageRequest).getContent();
    }
}