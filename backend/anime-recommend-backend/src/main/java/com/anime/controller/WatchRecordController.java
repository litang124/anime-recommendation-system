package com.anime.controller;

import com.anime.entity.WatchRecord;
import com.anime.service.WatchRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/watch")
@RequiredArgsConstructor
public class WatchRecordController {

    private final WatchRecordService watchRecordService;

    @PostMapping("/record")
    public Map<String, Object> create(@RequestBody WatchRecord watchRecord) {
        Map<String, Object> response = new HashMap<>();
        WatchRecord saved = watchRecordService.save(watchRecord);
        response.put("code", 200);
        response.put("message", "记录创建成功");
        response.put("data", saved);
        return response;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        List<WatchRecord> records = watchRecordService.findByUserId(userId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", records);
        return response;
    }

    @GetMapping("/anime/{animeId}")
    public Map<String, Object> getByAnime(@PathVariable Long animeId) {
        Map<String, Object> response = new HashMap<>();
        List<WatchRecord> records = watchRecordService.findByAnimeId(animeId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", records);
        return response;
    }

    @PutMapping("/progress")
    public Map<String, Object> updateProgress(
            @RequestParam Long userId,
            @RequestParam Long animeId,
            @RequestParam Integer progress,
            @RequestParam(required = false) Integer watchedEpisodes) {
        Map<String, Object> response = new HashMap<>();
        WatchRecord updated = watchRecordService.updateProgress(userId, animeId, progress, watchedEpisodes);
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updated);
        return response;
    }

    @GetMapping("/check")
    public Map<String, Object> checkRecord(
            @RequestParam Long userId,
            @RequestParam Long animeId) {
        Map<String, Object> response = new HashMap<>();
        WatchRecord record = watchRecordService.findByUserIdAndAnimeId(userId, animeId);
        if (record != null) {
            response.put("code", 200);
            response.put("message", "存在记录");
            response.put("data", record);
        } else {
            response.put("code", 404);
            response.put("message", "无观看记录");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        watchRecordService.deleteById(id);
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    /**
     * 收藏/取消收藏动漫
     * @param userId 用户 ID
     * @param animeId 动漫 ID
     * @param status 状态：0-想读 1-在读 2-已读，不传则切换状态
     */
    @PostMapping("/favorite")
    public Map<String, Object> favorite(
            @RequestParam Long userId,
            @RequestParam Long animeId,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            WatchRecord result = watchRecordService.toggleFavorite(userId, animeId, status);
            
            if (result == null) {
                response.put("code", 200);
                response.put("message", "已取消收藏");
                response.put("data", Map.of("favorited", false));
            } else {
                String statusText = switch (result.getStatus()) {
                    case 0 -> "想读";
                    case 1 -> "在读";
                    case 2 -> "已读";
                    default -> "未知";
                };
                response.put("code", 200);
                response.put("message", "收藏成功：" + statusText);
                response.put("data", Map.of(
                        "favorited", true,
                        "status", result.getStatus(),
                        "statusText", statusText,
                        "record", result
                ));
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "操作失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户的收藏列表
     * @param userId 用户 ID
     * @param status 状态筛选（可选）：0-想读 1-在读 2-已读
     */
    @GetMapping("/favorites")
    public Map<String, Object> getFavorites(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<WatchRecord> favorites = watchRecordService.findFavoritesByUserId(userId, status);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", favorites);
            response.put("count", favorites.size());
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check-favorite")
    public Map<String, Object> checkFavorite(
            @RequestParam Long userId,
            @RequestParam Long animeId) {
        Map<String, Object> response = new HashMap<>();
        
        WatchRecord record = watchRecordService.findByUserIdAndAnimeId(userId, animeId);
        
        if (record != null && record.getStatus() != null && record.getStatus() > 0) {
            response.put("code", 200);
            response.put("message", "已收藏");
            response.put("data", Map.of(
                    "favorited", true,
                    "status", record.getStatus(),
                    "record", record
            ));
        } else {
            response.put("code", 200);
            response.put("message", "未收藏");
            response.put("data", Map.of("favorited", false));
        }
        
        return response;
    }
}