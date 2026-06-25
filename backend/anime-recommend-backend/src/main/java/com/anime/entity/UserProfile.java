package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户画像实体
 */
@Data
@Entity
@Table(name = "user_profile")
public class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private Long userId;
    
    // 移除 OneToOne 关联，避免保存时需要真实 User 对象的问题
    // 直接通过 userId 关联即可
    
    @Column(columnDefinition = "TEXT")
    private String favoriteCategories;    // 偏好分类（JSON 格式）
    
    @Column(columnDefinition = "TEXT")
    private String favoriteTags;          // 偏好标签（JSON 格式）
    
    @Column(columnDefinition = "TEXT")
    private String wishList;              // 想看列表（JSON 格式，如 [1,5,16]）
    
    @Column(name = "total_views")
    private Integer totalViews = 0;       // 总观看数
    
    @Column(name = "total_favorites")
    private Integer totalFavorites = 0;   // 总收藏数
    
    @Column(name = "avg_score", precision = 3, scale = 1)
    private Double avgScore = 0.0;        // 平均评分
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column(name = "update_time")
    private LocalDateTime updateTime = LocalDateTime.now();
}
