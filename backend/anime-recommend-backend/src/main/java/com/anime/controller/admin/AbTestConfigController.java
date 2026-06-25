package com.anime.controller.admin;

import com.anime.annotation.AdminRequired;
import com.anime.service.AbTestConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A/B 测试配置管理接口
 */
@RestController
@RequestMapping("/admin/ab")
@RequiredArgsConstructor
@Slf4j
public class AbTestConfigController {

    private final AbTestConfigService abTestConfigService;

    /**
     * 获取当前 A/B 配置
     */
    @GetMapping("/config")
    @AdminRequired(operationType = "AB_CONFIG_READ", description = "读取 A/B 测试配置")
    public Map<String, Object> getConfig() {
        try {
            Map<String, Object> config = abTestConfigService.getConfig();
            return Map.of(
                    "code", 200,
                    "message", "success",
                    "data", config
            );
        } catch (Exception e) {
            log.error("获取 A/B 配置失败", e);
            return Map.of(
                    "code", 500,
                    "message", "获取失败：" + e.getMessage(),
                    "data", new HashMap<>()
            );
        }
    }

    /**
     * 更新 A/B 配置
     */
    @PostMapping("/config")
    @AdminRequired(operationType = "AB_CONFIG_WRITE", description = "更新 A/B 测试配置")
    public Map<String, Object> updateConfig(@RequestBody Map<String, Object> config) {
        try {
            abTestConfigService.updateConfig(config);
            return Map.of(
                    "code", 200,
                    "message", "配置已更新",
                    "data", config
            );
        } catch (Exception e) {
            log.error("更新 A/B 配置失败", e);
            return Map.of(
                    "code", 500,
                    "message", "更新失败：" + e.getMessage(),
                    "data", new HashMap<>()
            );
        }
    }
}