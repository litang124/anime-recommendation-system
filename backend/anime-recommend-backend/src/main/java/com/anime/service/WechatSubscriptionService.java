package com.anime.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信订阅消息服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WechatSubscriptionService {
    
    @Value("${wechat.template.schedule-reminder:}")
    private String scheduleReminderTemplateId;
    
    @Value("${wechat.template.recommend-reminder:}")
    private String recommendReminderTemplateId;
    
    @Value("${wechat.template.review-reply:}")
    private String reviewReplyTemplateId;
    
    private final WechatService wechatService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 发送新番播出提醒
     */
    public boolean sendScheduleReminder(String openid, String animeTitle, 
                                       String episode, String airTime) {
        try {
            String accessToken = wechatService.getAccessToken();
            String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
            
            Map<String, Object> message = new HashMap<>();
            message.put("touser", openid);
            message.put("template_id", scheduleReminderTemplateId);
            message.put("page", "pages/anime/detail?id=" + animeTitle);
            
            // 订阅消息数据
            Map<String, Object> data = new HashMap<>();
            data.put("thing1", Map.of("value", animeTitle));
            data.put("character_string2", Map.of("value", episode));
            data.put("time3", Map.of("value", airTime));
            message.put("data", data);
            
            String requestBody = objectMapper.writeValueAsString(message);
            String response = restTemplate.postForObject(url, requestBody, String.class);
            
            log.info("发送新番提醒成功: {}", response);
            return true;
            
        } catch (Exception e) {
            log.error("发送新番提醒失败", e);
            return false;
        }
    }
    
    /**
     * 发送推荐提醒
     */
    public boolean sendRecommendReminder(String openid, String animeTitle, String reason) {
        try {
            String accessToken = wechatService.getAccessToken();
            String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
            
            Map<String, Object> message = new HashMap<>();
            message.put("touser", openid);
            message.put("template_id", recommendReminderTemplateId);
            message.put("page", "pages/recommend/index");
            
            Map<String, Object> data = new HashMap<>();
            data.put("thing1", Map.of("value", animeTitle));
            data.put("thing2", Map.of("value", reason));
            message.put("data", data);
            
            String requestBody = objectMapper.writeValueAsString(message);
            restTemplate.postForObject(url, requestBody, String.class);
            
            log.info("发送推荐提醒成功");
            return true;
            
        } catch (Exception e) {
            log.error("发送推荐提醒失败", e);
            return false;
        }
    }
    
    /**
     * 获取订阅模板列表
     */
    public Map<String, String> getSubscriptionTemplates() {
        Map<String, String> templates = new HashMap<>();
        templates.put("scheduleReminder", scheduleReminderTemplateId);
        templates.put("recommendReminder", recommendReminderTemplateId);
        templates.put("reviewReply", reviewReplyTemplateId);
        return templates;
    }
    
    /**
     * 检查用户是否订阅
     * 注：微信小程序需要用户主动触发订阅，后端无法直接查询
     */
    public boolean checkUserSubscription(String openid, String templateId) {
        // 微信不提供查询接口，只能在发送时判断
        log.info("检查用户订阅状态: openid={}, templateId={}", openid, templateId);
        return true; // 默认返回true，实际以发送结果为准
    }
    
    /**
     * 添加订阅（仅记录，实际订阅由小程序端完成）
     */
    public boolean addSubscription(String openid, String templateId, 
                                   Map<String, String> sceneData) {
        log.info("记录订阅信息: openid={}, templateId={}", openid, templateId);
        // 这里可以将订阅信息存入数据库
        return true;
    }
}
