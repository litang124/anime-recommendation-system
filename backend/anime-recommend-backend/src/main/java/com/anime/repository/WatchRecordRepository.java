package com.anime.repository;

import com.anime.entity.WatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WatchRecordRepository extends JpaRepository<WatchRecord, Long> {
    List<WatchRecord> findByUserId(Long userId);
    List<WatchRecord> findByAnimeId(Long animeId);
    WatchRecord findByUserIdAndAnimeId(Long userId, Long animeId);
    
    /**
     * 查询最近创建时间之后的用户ID列表
     */
    List<Long> findUserIdsByCreateTimeAfter(java.time.LocalDateTime createTime);
}