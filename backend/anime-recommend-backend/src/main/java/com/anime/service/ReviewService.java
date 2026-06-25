package com.anime.service;

import com.anime.entity.Review;
import java.util.List;

public interface ReviewService {
    Review save(Review review);
    Review findById(Long id);
    List<Review> findByAnimeId(Long animeId);
    List<Review> findByUserId(Long userId);
    List<Review> findHotByAnimeId(Long animeId);
    List<Review> findNewByAnimeId(Long animeId);
    Review likeReview(Long reviewId);
    void deleteById(Long id);
    
    /**
     * 获取所有评论
     */
    List<Review> findAll();
    
    /**
     * 回复评论
     */
    Review replyReview(Long parentId, Long userId, String content, Double rating);
    
    /**
     * 查询某条评论的所有回复
     */
    List<Review> getReplies(Long parentId);
    
    /**
     * 查询动漫下的评论（包含回复）
     */
    List<Review> getCommentsWithReplies(Long animeId);
    
    /**
     * 获取个性化社区推荐评论
     * @param userId 用户ID
     * @param limit 返回数量
     */
    List<Review> getPersonalizedCommunityReviews(Long userId, Integer limit);

    /**
     * 取消点赞某条评论
     * @param reviewId 评论ID
     * @return 更新后的评论对象，若评论不存在或已无点赞则返回 null
     */
    Review unlikeReview(Long reviewId);

    /**
     * 获取最新评论（全局，按时间倒序）
     * @param page 页码
     * @param size 每页数量
     * @return 评论列表
     */
    List<Review> getLatestReviews(Integer page, Integer size);
}