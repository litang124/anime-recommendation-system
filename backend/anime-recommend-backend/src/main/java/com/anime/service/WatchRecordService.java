package com.anime.service;

import com.anime.entity.WatchRecord;
import java.util.List;

public interface WatchRecordService {
    WatchRecord save(WatchRecord watchRecord);
    WatchRecord findByUserIdAndAnimeId(Long userId, Long animeId);
    List<WatchRecord> findByUserId(Long userId);
    List<WatchRecord> findByAnimeId(Long animeId);
    WatchRecord updateProgress(Long userId, Long animeId, Integer progress, Integer watchedEpisodes);
    void deleteById(Long id);
    
    /**
     * 收藏/取消收藏动漫
     * @param userId 用户 ID
     * @param animeId 动漫 ID
     * @param status 状态：0-想读 1-在读 2-已读
     * @return 观看记录
     */
    WatchRecord toggleFavorite(Long userId, Long animeId, Integer status);
    
    /**
     * 获取用户的收藏列表
     * @param userId 用户 ID
     * @param status 状态筛选（可选）：0-想读 1-在读 2-已读
     * @return 收藏列表
     */
    List<WatchRecord> findFavoritesByUserId(Long userId, Integer status);
}