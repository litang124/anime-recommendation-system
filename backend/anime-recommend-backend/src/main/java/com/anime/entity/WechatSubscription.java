package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 微信订阅消息配置
 */
@Data
@Entity
@Table(name = "wechat_subscription")
public class WechatSubscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 微信openid
     */
    @Column(name = "openid", nullable = false)
    private String openid;
    
    /**
     * 订阅的动漫ID（多个用逗号分隔）
     */
    @Column(name = "subscribed_anime_ids", columnDefinition = "TEXT")
    private String subscribedAnimeIds;
    
    /**
     * 是否启用新番提醒
     */
    @Column(name = "enable_new_anime_notify")
    private Boolean enableNewAnimeNotify = true;
    
    /**
     * 是否启用推荐提醒
     */
    @Column(name = "enable_recommend_notify")
    private Boolean enableRecommendNotify = true;
    
    /**
     * 提醒时间（分钟），默认提前30分钟
     */
    @Column(name = "notify_minutes_before")
    private Integer notifyMinutesBefore = 30;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
    
    @Column(name = "update_time")
    private LocalDateTime updateTime = LocalDateTime.now();
}
