package com.anime.repository;

import com.anime.entity.AdminOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员操作日志仓库
 */
@Repository
public interface AdminOperationLogRepository extends JpaRepository<AdminOperationLog, Long> {
    
    /**
     * 按管理员 ID 查询
     */
    List<AdminOperationLog> findByAdminIdOrderByCreateTimeDesc(Long adminId);
    
    /**
     * 按操作类型查询
     */
    List<AdminOperationLog> findByOperationTypeOrderByCreateTimeDesc(String operationType);
    
    /**
     * 按时间范围查询
     */
    List<AdminOperationLog> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按管理员 ID 和时间范围分页查询
     */
    Page<AdminOperationLog> findByAdminIdAndCreateTimeBetween(
        Long adminId, 
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        Pageable pageable
    );
    
    /**
     * 按操作状态查询
     */
    List<AdminOperationLog> findByStatusOrderByCreateTimeDesc(Integer status);
}
