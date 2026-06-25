package com.anime.controller.admin;

import com.anime.annotation.AdminRequired;
import com.anime.entity.AnimeSchedule;
import com.anime.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新番日程管理（后台）
 */
@RestController
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 获取日程列表
     */
    @GetMapping("/list")
    @AdminRequired(operationType = "SCHEDULE_LIST", description = "查询日程列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long animeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AnimeSchedule> schedules;
            
            if (animeId != null) {
                // 按动漫 ID 查询
                schedules = scheduleService.getByAnimeId(animeId);
            } else if (date != null) {
                // 按日期查询
                schedules = scheduleService.getByDate(date);
            } else {
                // 获取本周日程
                schedules = scheduleService.getThisWeek();
            }
            
            // 手动分页
            int start = page * size;
            int end = Math.min(start + size, schedules.size());
            List<AnimeSchedule> pageSchedules = schedules.subList(start, end);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", pageSchedules);
            response.put("total", schedules.size());
            response.put("pages", (int) Math.ceil((double) schedules.size() / size));
            response.put("currentPage", page);
            
        } catch (Exception e) {
            log.error("获取日程列表失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 创建日程
     */
    @PostMapping("/create")
    @AdminRequired(operationType = "SCHEDULE_ADD", description = "添加日程")
    public Map<String, Object> create(@RequestBody AnimeSchedule schedule) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AnimeSchedule saved = scheduleService.save(schedule);
            
            response.put("code", 200);
            response.put("message", "创建成功");
            response.put("data", saved);
            
        } catch (Exception e) {
            log.error("创建日程失败", e);
            response.put("code", 500);
            response.put("message", "创建失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新日程
     */
    @PutMapping("/{id}")
    @AdminRequired(operationType = "SCHEDULE_UPDATE", description = "更新日程")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestBody AnimeSchedule schedule) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            AnimeSchedule existing = scheduleService.findById(id);
            if (existing == null) {
                response.put("code", 404);
                response.put("message", "日程不存在");
                return response;
            }
            
            // 更新字段
            if (schedule.getAnimeId() != null) existing.setAnimeId(schedule.getAnimeId());
            if (schedule.getEpisode() != null) existing.setEpisode(schedule.getEpisode());
            if (schedule.getAirTime() != null) existing.setAirTime(schedule.getAirTime());
            if (schedule.getPlatform() != null) existing.setPlatform(schedule.getPlatform());
            if (schedule.getPlatformUrl() != null) existing.setPlatformUrl(schedule.getPlatformUrl());
            if (schedule.getStatus() != null) existing.setStatus(schedule.getStatus());
            
            AnimeSchedule updated = scheduleService.save(existing);
            
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updated);
            
        } catch (Exception e) {
            log.error("更新日程失败", e);
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除日程
     */
    @DeleteMapping("/{id}")
    @AdminRequired(operationType = "SCHEDULE_DELETE", description = "删除日程")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            scheduleService.deleteById(id);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            log.error("删除日程失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 批量导入日程
     */
    @PostMapping("/batch-import")
    @AdminRequired(operationType = "SCHEDULE_BATCH_IMPORT", description = "批量导入日程")
    public Map<String, Object> batchImport(@RequestBody List<AnimeSchedule> schedules) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int successCount = 0;
            int failCount = 0;
            
            for (AnimeSchedule schedule : schedules) {
                try {
                    scheduleService.save(schedule);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入日程失败：{}", schedule.getAnimeId(), e);
                    failCount++;
                }
            }
            
            response.put("code", 200);
            response.put("message", "批量导入完成");
            response.put("data", Map.of(
                    "total", schedules.size(),
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
     * 日程统计
     */
    @GetMapping("/statistics")
    @AdminRequired(operationType = "SCHEDULE_STATISTICS", description = "日程统计")
    public Map<String, Object> statistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AnimeSchedule> allSchedules = scheduleService.getThisWeek();
            
            // 总日程数
            long totalCount = allSchedules.size();
            
            // 按星期几分组
            Map<Integer, Long> dayOfWeekDistribution = allSchedules.stream()
                    .filter(s -> s.getAirTime() != null)
                    .collect(java.util.stream.Collectors.groupingBy(
                            s -> s.getAirTime().getDayOfWeek().getValue(), // 1=周一，7=周日
                            java.util.stream.Collectors.counting()
                    ));
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", Map.of(
                    "total", totalCount,
                    "dayOfWeekDistribution", dayOfWeekDistribution
            ));
            
        } catch (Exception e) {
            log.error("获取日程统计失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }
}
