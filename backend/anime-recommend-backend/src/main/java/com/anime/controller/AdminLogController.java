package com.anime.controller;

import com.anime.entity.AdminOperationLog;
import com.anime.service.AdminOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员操作日志管理接口
 */
@RestController
@RequestMapping("/admin/log")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "管理员操作日志管理")
public class AdminLogController {

    private final AdminOperationLogService adminOperationLogService;

    @GetMapping
    @ApiOperation("分页查询操作日志")
    public Map<String, Object> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<AdminOperationLog> logs = adminOperationLogService.findAll(
                PageRequest.of(page, size)
            );
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", logs.getContent());
            response.put("total", logs.getTotalElements());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", logs.getTotalPages());
            
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 ID 查询操作日志")
    public Map<String, Object> getLogById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AdminOperationLog log = adminOperationLogService.findById(id);
            
            if (log != null) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", log);
            } else {
                response.put("code", 404);
                response.put("message", "日志不存在");
            }
            
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/admin/{adminId}")
    @ApiOperation("按管理员 ID 查询操作日志")
    public Map<String, Object> getLogsByAdminId(@PathVariable Long adminId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AdminOperationLog> logs = adminOperationLogService.findByAdminId(adminId);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", logs);
            response.put("count", logs.size());
            
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/time-range")
    @ApiOperation("按时间范围查询操作日志")
    public Map<String, Object> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<AdminOperationLog> logs = adminOperationLogService.findByTimeRange(startTime, endTime);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", logs);
            response.put("count", logs.size());
            
        } catch (Exception e) {
            log.error("查询操作日志失败", e);
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

    @DeleteMapping("/old")
    @ApiOperation("删除过期日志（30 天前）")
    public Map<String, Object> deleteOldLogs() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime beforeTime = LocalDateTime.now().minusDays(30);
            adminOperationLogService.deleteOldLogs(beforeTime);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            log.error("删除过期日志失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }
}
