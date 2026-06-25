package com.anime.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "anime")
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;           // 标题

    private String coverUrl;        // 封面图

    @Column(columnDefinition = "TEXT")
    private String description;     // 描述

    @Column(precision = 3, scale = 1)
    private Double score = 0.0;     // 评分

    private Integer episodeCount = 0; // 集数

    private Integer status = 0;     // 状态：0-连载，1-完结

    private String tags;            // 标签，逗号分隔

    private String recommendReason; // 推荐理由

    private Integer heat = 0;       // 热度

    @Column(name = "release_year")
    private Integer releaseYear;    // 上映年份

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    // 关联关系
    @OneToMany(mappedBy = "anime", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("anime")
    private List<AnimeSchedule> schedules; // 放送计划

    @OneToMany(mappedBy = "anime", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("anime")
    private List<Review> reviews;   // 评论

    @OneToMany(mappedBy = "anime", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("anime")
    private List<WatchRecord> watchRecords; // 观看记录
}