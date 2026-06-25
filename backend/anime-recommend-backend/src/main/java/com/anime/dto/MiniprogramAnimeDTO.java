package com.anime.dto;

import lombok.Data;

/**
 * 小程序精简版动漫信息DTO
 */
@Data
public class MiniprogramAnimeDTO {
    private Long id;
    private String title;
    private String coverUrl;
    private Double score;
    private Integer status;
    private String tags;
    private String recommendReason; // 推荐理由
    private Integer heat;
    private Integer releaseYear;
    
    // 简化字段，减少小程序端数据传输
    public MiniprogramAnimeDTO() {}
    
    public MiniprogramAnimeDTO(Long id, String title, String coverUrl, 
                               Double score, Integer status, String tags) {
        this.id = id;
        this.title = title;
        this.coverUrl = (coverUrl != null && !coverUrl.trim().isEmpty()) ? coverUrl : "/images/default-cover.png";
        this.score = score;
        this.status = status;
        this.tags = tags;
    }
}
