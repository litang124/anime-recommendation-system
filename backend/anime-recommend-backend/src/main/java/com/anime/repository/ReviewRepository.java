package com.anime.repository;

import com.anime.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAnimeId(Long animeId);
    List<Review> findByUserId(Long userId);
    List<Review> findByAnimeIdOrderByLikeCountDesc(Long animeId);
    List<Review> findByAnimeIdOrderByCreateTimeDesc(Long animeId);
    
    /**
     * 查询某条评论的所有回复
     */
    List<Review> findByParentIdOrderByCreateTimeAsc(Long parentId);
    
    /**
     * 查询动漫下的所有一级评论（不包括回复）
     */
    List<Review> findByAnimeIdAndParentIdIsNullOrderByCreateTimeDesc(Long animeId);
    
    /**
     * 查询最近创建时间之后的用户ID列表
     */
    List<Long> findUserIdsByCreateTimeAfter(java.time.LocalDateTime createTime);
}