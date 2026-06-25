package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 推荐记录实体
 */
@Data
@Entity
@Table(name = "recommendation")
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "anime_id", nullable = false)
    private Long animeId;
    
    /**
     * 推荐分数 (0-1)
     */
    @Column(precision = 5, scale = 4)
    private Double score;
    
    /**
     * 推荐类型：1-协同过滤 2-内容推荐 3-热门推荐
     */
    private Integer type;
    
    /**
     * 推荐原因
     */
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    /**
     * 是否已点击查看
     */
    @Column(name = "is_clicked")
    private Boolean isClicked = false;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
