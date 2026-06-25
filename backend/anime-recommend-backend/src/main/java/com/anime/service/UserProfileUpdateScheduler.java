package com.anime.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户画像定时更新服务
 * 定期更新活跃用户的画像，确保推荐算法的实时性
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileUpdateScheduler {
    
    private final UserProfileService userProfileService;
    
    /**
     * 每小时执行一次，更新过去24小时内有活跃行为的用户画像
     * 使用 @Scheduled(cron = "0 0 * * * ?") 表示每小时执行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateActiveUserProfiles() {
        log.info("开始执行用户画像定时更新任务...");
        
        try {
            // 获取最近24小时内有活跃行为的用户ID列表
            List<Long> activeUserIds = userProfileService.getActiveUserIds();
            
            log.info("检测到 {} 个活跃用户需要更新画像", activeUserIds.size());
            
            // 更新活跃用户的画像
            for (Long userId : activeUserIds) {
                try {
                    userProfileService.updateUserProfile(userId);
                    log.debug("已更新用户画像：{}", userId);
                } catch (Exception e) {
                    log.warn("更新用户 {} 的画像失败：{}", userId, e.getMessage());
                }
            }
            
            log.info("用户画像定时更新任务执行完成");
            
        } catch (Exception e) {
            log.error("用户画像定时更新任务执行失败", e);
        }
    }
}
