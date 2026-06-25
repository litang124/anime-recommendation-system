package com.anime.controller;

import com.anime.entity.AnimeSchedule;
import com.anime.entity.UserReminder;
import com.anime.repository.UserReminderRepository;
import com.anime.service.ScheduleService;
import com.anime.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserReminderRepository userReminderRepository;
    private final UserProfileService userProfileService;

    @GetMapping("/day")
    public Map<String, Object> getByDay(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Map<String, Object> response = new HashMap<>();
        
        // 如果前端传了startTime，从中提取日期
        if (date == null && startTime != null && !startTime.isEmpty()) {
            try {
                // startTime格式：2026-01-10T00:00:00，提取日期部分
                date = LocalDate.parse(startTime.substring(0, 10));
            } catch (Exception e) {
                // 如果解析失败，尝试完整解析
                try {
                    date = LocalDate.parse(startTime);
                } catch (Exception ex) {
                    response.put("code", 400);
                    response.put("message", "日期格式错误");
                    response.put("data", List.of());
                    return response;
                }
            }
        }
        
        // 如果都没传，使用今天
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<AnimeSchedule> schedules = scheduleService.getByDate(date);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", schedules);
        return response;
    }

    @GetMapping("/range")
    public Map<String, Object> getByDateRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate startDate = parseDateParam(startTime);
            LocalDate endDate = parseDateParam(endTime);
            
            if (startDate == null || endDate == null) {
                response.put("code", 400);
                response.put("message", "日期格式错误");
                response.put("data", List.of());
                return response;
            }
            
            List<AnimeSchedule> schedules = scheduleService.getByDateRange(startDate, endDate);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", schedules);
            
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", "日期格式错误: " + e.getMessage());
            response.put("data", List.of());
        }
        
        return response;
    }
    
    @GetMapping("/week")
    public Map<String, Object> getThisWeek() {
        Map<String, Object> response = new HashMap<>();
        List<AnimeSchedule> schedules = scheduleService.getThisWeek();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", schedules);
        return response;
    }
    
    private LocalDate parseDateParam(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        try {
            // 尝试解析完整的时间戳字符串 (如: 2026-01-10T00:00:00)
            if (dateStr.contains("T")) {
                return LocalDate.parse(dateStr.substring(0, 10));
            }
            // 尝试解析日期字符串 (如: 2026-01-10)
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            // 解析失败，返回null
            return null;
        }
    }

    @GetMapping("/anime/{animeId}")
    public Map<String, Object> getByAnime(@PathVariable Long animeId) {
        Map<String, Object> response = new HashMap<>();
        List<AnimeSchedule> schedules = scheduleService.getByAnimeId(animeId);
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", schedules);
        return response;
    }

    @PostMapping("/create")
    public Map<String, Object> create(@RequestBody AnimeSchedule schedule) {
        Map<String, Object> response = new HashMap<>();
        AnimeSchedule saved = scheduleService.save(schedule);
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", saved);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        scheduleService.deleteById(id);
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }

    @PostMapping("/remind")
    public Map<String, Object> setReminder(
            @RequestParam Long userId,
            @RequestParam Long animeId,
            @RequestParam Long scheduleId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. 创建提醒记录
            UserReminder reminder = new UserReminder();
            reminder.setUserId(userId);
            reminder.setAnimeId(animeId);
            reminder.setScheduleId(scheduleId);
            reminder.setRemindTime(LocalDateTime.now().plusMinutes(30)); // 默认播出前30分钟提醒
            reminder.setStatus(0);
            userReminderRepository.save(reminder);

            // 2. 添加到用户想看列表
            userProfileService.addWishAnime(userId, animeId);

            response.put("code", 200);
            response.put("message", "提醒已设置，已加入想看清单");
            response.put("data", reminder);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "设置提醒失败：" + e.getMessage());
        }
        return response;
    }
}