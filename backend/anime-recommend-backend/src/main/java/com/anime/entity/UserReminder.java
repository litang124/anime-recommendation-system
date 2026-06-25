package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户新番提醒实体
 */
@Data
@Entity
@Table(name = "user_reminder")
public class UserReminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "anime_id", nullable = false)
    private Long animeId;
    
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;
    
    @Column(name = "remind_time")
    private LocalDateTime remindTime; // 提醒时间（如播出前30分钟）
    
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0; // 0:待提醒 1:已提醒 2:已取消
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}