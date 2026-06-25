package com.anime.service.impl;

import com.anime.entity.WatchRecord;
import com.anime.repository.WatchRecordRepository;
import com.anime.service.WatchRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchRecordServiceImpl implements WatchRecordService {

    private final WatchRecordRepository watchRecordRepository;

    @Override
    public WatchRecord save(WatchRecord watchRecord) {
        if (watchRecord.getCreateTime() == null) {
            watchRecord.setCreateTime(LocalDateTime.now());
        }
        watchRecord.setUpdateTime(LocalDateTime.now());
        return watchRecordRepository.save(watchRecord);
    }

    @Override
    public WatchRecord findByUserIdAndAnimeId(Long userId, Long animeId) {
        return watchRecordRepository.findByUserIdAndAnimeId(userId, animeId);
    }

    @Override
    public List<WatchRecord> findByUserId(Long userId) {
        return watchRecordRepository.findByUserId(userId);
    }

    @Override
    public List<WatchRecord> findByAnimeId(Long animeId) {
        return watchRecordRepository.findByAnimeId(animeId);
    }

    @Override
    @Transactional
    public WatchRecord updateProgress(Long userId, Long animeId, Integer progress, Integer watchedEpisodes) {
        WatchRecord record = findByUserIdAndAnimeId(userId, animeId);
        if (record == null) {
            // 创建新的观看记录
            record = new WatchRecord();
            record.setUserId(userId);
            record.setAnimeId(animeId);
        }

        if (progress != null) {
            record.setProgress(progress);
        }

        if (watchedEpisodes != null) {
            record.setWatchedEpisodes(watchedEpisodes);
        }

        // 自动更新状态
        if (record.getProgress() >= 100) {
            record.setStatus(2); // 已读
        } else if (record.getProgress() > 0) {
            record.setStatus(1); // 在读
        }

        record.setUpdateTime(LocalDateTime.now());
        return watchRecordRepository.save(record);
    }

    @Override
    public void deleteById(Long id) {
        watchRecordRepository.deleteById(id);
    }

    @Override
    @Transactional
    public WatchRecord toggleFavorite(Long userId, Long animeId, Integer status) {
        // 查找现有记录
        WatchRecord record = findByUserIdAndAnimeId(userId, animeId);
        
        if (record == null) {
            // 创建新记录（收藏）
            record = new WatchRecord();
            record.setUserId(userId);
            record.setAnimeId(animeId);
            record.setStatus(status != null ? status : 0); // 默认 0-想读
            record.setCreateTime(LocalDateTime.now());
        } else {
            // 如果状态相同，则取消收藏（删除记录）
            if (record.getStatus().equals(status != null ? status : 0)) {
                deleteById(record.getId());
                return null;
            }
            // 更新状态
            record.setStatus(status != null ? status : record.getStatus());
        }
        
        record.setUpdateTime(LocalDateTime.now());
        return watchRecordRepository.save(record);
    }

    @Override
    public List<WatchRecord> findFavoritesByUserId(Long userId, Integer status) {
        List<WatchRecord> allRecords = watchRecordRepository.findByUserId(userId);
        
        if (status == null) {
            // 返回所有有状态的记录（排除 status=0 且 progress=0 的纯浏览记录）
            return allRecords.stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() > 0)
                    .toList();
        } else {
            // 按状态筛选
            return allRecords.stream()
                    .filter(r -> status.equals(r.getStatus()))
                    .toList();
        }
    }
}