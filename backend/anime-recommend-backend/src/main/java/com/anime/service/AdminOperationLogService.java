package com.anime.service;

import com.anime.entity.AdminOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员操作日志服务接口
 */
public interface AdminOperationLogService {
    
    /**
     * 保存操作日志
     */
    AdminOperationLog save(AdminOperationLog log);
    
    /**
     * 按 ID 查询
     */
    AdminOperationLog findById(Long id);
    
    /**
     * 按管理员 ID 查询
     */
    List<AdminOperationLog> findByAdminId(Long adminId);
    
    /**
     * 分页查询
     */
    Page<AdminOperationLog> findAll(Pageable pageable);
    
    /**
     * 按时间范围查询
     */
    List<AdminOperationLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 删除过期日志（30 天前）
     */
    void deleteOldLogs(LocalDateTime beforeTime);
}
