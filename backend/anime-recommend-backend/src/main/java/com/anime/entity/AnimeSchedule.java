package com.anime.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "anime_schedule")
public class AnimeSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anime_id", nullable = false)
    private Long animeId;

    private Integer episode;        // 第几集
    @Column(name = "air_time")  // 显式映射数据库列名
    private LocalDateTime airTime;  // 播出时间
    private String platform;        // 平台：B站、爱奇艺等
    private String platformUrl;     // 观看链接
    private Integer status = 0;     // 0:未播 1:已播

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", insertable = false, updatable = false)
    private Anime anime;           // 关联的动漫
}