package com.anime.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "anime_id", nullable = false)
    private Long animeId;

    @Column(precision = 3, scale = 1)
    private Double rating;          // 评分1-5

    @Column(columnDefinition = "TEXT")
    private String content;         // 短评内容

    private Integer likeCount = 0;  // 点赞数
    
    private String recommendReason; // 推荐理由
    
    @Column(name = "parent_id")
    private Long parentId;          // 父评论 ID（如果是回复，指向被回复的评论）
    
    @Column(name = "reply_user_id")
    private Long replyUserId;       // 被回复的用户 ID
    
    @Column(name = "reply_username")
    private String replyUsername;   // 被回复的用户名

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"schedules", "reviews", "watchRecords"})
    private Anime anime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"reviews", "watchRecords"})
    private User user;
}