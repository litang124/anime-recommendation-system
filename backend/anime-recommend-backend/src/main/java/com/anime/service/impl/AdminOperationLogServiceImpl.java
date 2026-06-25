package com.anime.service.impl;

import com.anime.entity.AdminOperationLog;
import com.anime.repository.AdminOperationLogRepository;
import com.anime.service.AdminOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员操作日志服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOperationLogServiceImpl implements AdminOperationLogService {

    private final AdminOperationLogRepository adminOperationLogRepository;

    @Override
    public AdminOperationLog save(AdminOperationLog log) {
        if (log.getCreateTime() == null) {
            log.setCreateTime(LocalDateTime.now());
        }
        return adminOperationLogRepository.save(log);
    }

    @Override
    public AdminOperationLog findById(Long id) {
        return adminOperationLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<AdminOperationLog> findByAdminId(Long adminId) {
        return adminOperationLogRepository.findByAdminIdOrderByCreateTimeDesc(adminId);
    }

    @Override
    public Page<AdminOperationLog> findAll(Pageable pageable) {
        return adminOperationLogRepository.findAll(pageable);
    }

    @Override
    public List<AdminOperationLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return adminOperationLogRepository.findByCreateTimeBetween(startTime, endTime);
    }

    @Override
    @Transactional
    public void deleteOldLogs(LocalDateTime beforeTime) {
        log.info("删除 {} 之前的操作日志", beforeTime);
        List<AdminOperationLog> oldLogs = findByTimeRange(LocalDateTime.of(2000, 1, 1, 0, 0), beforeTime);
        adminOperationLogRepository.deleteAll(oldLogs);
        log.info("删除了 {} 条过期日志", oldLogs.size());
    }
}
