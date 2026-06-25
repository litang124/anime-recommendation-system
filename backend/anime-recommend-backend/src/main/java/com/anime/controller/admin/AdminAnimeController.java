package com.anime.controller.admin;

import com.anime.annotation.AdminRequired;
import com.anime.entity.Anime;
import com.anime.entity.Review;
import com.anime.entity.User;
import com.anime.service.AnimeService;
import com.anime.service.ReviewService;
import com.anime.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动漫信息管理（后台）
 */
@RestController
@RequestMapping("/admin/anime")
@RequiredArgsConstructor
@Slf4j
public class AdminAnimeController {

    private final AnimeService animeService;

    /**
     * 获取动漫列表（支持筛选）
     */
    @GetMapping("/list")
    @AdminRequired(operationType = "ANIME_LIST", description = "查询动漫列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String tag) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<Anime> animePage;
            
            if (tag != null && !tag.isEmpty()) {
                // 按标签查询
                List<Anime> byTag = animeService.findByTag(tag);
                int start = page * size;
                int end = Math.min(start + size, byTag.size());
                List<Anime> content = byTag.subList(start, end);
                animePage = new org.springframework.data.domain.PageImpl<>(content, 
                        org.springframework.data.domain.PageRequest.of(page, size), byTag.size());
            } else if (year != null) {
                // 按年份查询
                List<Anime> byYear = animeService.findByYear(year);
                int start = page * size;
                int end = Math.min(start + size, byYear.size());
                List<Anime> content = byYear.subList(start, end);
                animePage = new org.springframework.data.domain.PageImpl<>(content, 
                        org.springframework.data.domain.PageRequest.of(page, size), byYear.size());
            } else if (status != null) {
                // 按状态查询
                List<Anime> byStatus = animeService.findByStatus(status);
                int start = page * size;
                int end = Math.min(start + size, byStatus.size());
                List<Anime> content = byStatus.subList(start, end);
                animePage = new org.springframework.data.domain.PageImpl<>(content, 
                        org.springframework.data.domain.PageRequest.of(page, size), byStatus.size());
            } else {
                // 分页查询所有
                animePage = animeService.findAllWithPagination(page, size);
            }
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", animePage.getContent());
            response.put("total", animePage.getTotalElements());
            response.put("pages", animePage.getTotalPages());
            response.put("currentPage", page);
            
        } catch (Exception e) {
            log.error("获取动漫列表失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 创建动漫
     */
    @PostMapping("/create")
    @AdminRequired(operationType = "ANIME_ADD", description = "添加动漫")
    public Map<String, Object> create(@RequestBody Anime anime) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Anime saved = animeService.save(anime);
            
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", saved);
            
        } catch (Exception e) {
            log.error("创建动漫失败", e);
            response.put("code", 500);
            response.put("message", "创建失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新动漫信息
     */
    @PutMapping("/{id}")
    @AdminRequired(operationType = "ANIME_UPDATE", description = "更新动漫")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestBody Anime anime) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Anime existing = animeService.findById(id);
            if (existing == null) {
                response.put("code", 404);
                response.put("message", "动漫不存在");
                return response;
            }
            
            // 更新字段
            if (anime.getTitle() != null) existing.setTitle(anime.getTitle());
            if (anime.getCoverUrl() != null) existing.setCoverUrl(anime.getCoverUrl());
            if (anime.getDescription() != null) existing.setDescription(anime.getDescription());
            if (anime.getEpisodeCount() != null) existing.setEpisodeCount(anime.getEpisodeCount());
            if (anime.getStatus() != null) existing.setStatus(anime.getStatus());
            if (anime.getTags() != null) existing.setTags(anime.getTags());
            if (anime.getReleaseYear() != null) existing.setReleaseYear(anime.getReleaseYear());
            if (anime.getScore() != null) existing.setScore(anime.getScore());
            
            Anime updated = animeService.save(existing);
            
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updated);
            
        } catch (Exception e) {
            log.error("更新动漫失败", e);
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除动漫
     */
    @DeleteMapping("/{id}")
    @AdminRequired(level = 1, operationType = "ANIME_DELETE", description = "删除动漫")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            animeService.deleteById(id);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            log.error("删除动漫失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 批量导入动漫（预留接口）
     */
    @PostMapping("/batch-import")
    @AdminRequired(level = 1, operationType = "ANIME_BATCH_IMPORT", description = "批量导入动漫")
    public Map<String, Object> batchImport(@RequestBody List<Anime> animes) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (Anime anime : animes) {
                try {
                    animeService.save(anime);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入动漫失败：{}", anime.getTitle(), e);
                    failCount++;
                }
            }
            
            response.put("code", 200);
            response.put("message", "批量导入完成");
            response.put("data", Map.of(
                    "total", animes.size(),
                    "success", successCount,
                    "fail", failCount
            ));
            
        } catch (Exception e) {
            log.error("批量导入失败", e);
            response.put("code", 500);
            response.put("message", "导入失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 数据统计
     */
    @GetMapping("/statistics")
    @AdminRequired(operationType = "ANIME_STATISTICS", description = "动漫统计")
    public Map<String, Object> statistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 总数统计
            long totalCount = animeService.findAllWithPagination(0, 1).getTotalElements();
            
            // 连载中的数量
            long ongoingCount = animeService.findByStatus(0).size();
            
            // 已完结的数量
            long completedCount = animeService.findByStatus(1).size();
            
            // 热度 TOP10
            List<Anime> hotTop10 = animeService.getHotAnime().stream().limit(10).toList();
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", Map.of(
                    "total", totalCount,
                    "ongoing", ongoingCount,
                    "completed", completedCount,
                    "hotTop10", hotTop10
            ));
            
        } catch (Exception e) {
            log.error("获取统计数据失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }
}
