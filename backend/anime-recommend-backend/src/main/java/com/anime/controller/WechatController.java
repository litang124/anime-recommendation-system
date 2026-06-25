package com.anime.controller;

import com.anime.entity.User;
import com.anime.service.UserService;
import com.anime.service.WechatService;
import com.anime.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序相关接口
 */
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
@Slf4j
public class WechatController {
    
    private final WechatService wechatService;
    private final UserService userService;
    private final TokenUtil tokenUtil;
    
    /**
     * 微信小程序登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        String code = params.get("code");
        
        try {
            log.info("微信登录请求，code: {}", code);
            
            // 1. 获取openid和session_key
            Map<String, String> sessionInfo = wechatService.getSessionInfo(code);
            String openid = sessionInfo.get("openid");
            String sessionKey = sessionInfo.get("session_key");
            
            log.info("获取到openid: {}", openid);
            
            // 2. 查找或创建用户
            User user = userService.findByOpenid(openid);
            if (user == null) {
                log.info("新用户，创建账号");
                user = new User();
                user.setOpenid(openid);
                user.setSessionKey(sessionKey);
                user.setNickname("动漫爱好者" + (System.currentTimeMillis() % 10000));
                user.setUsername("wx_" + openid.substring(0, 10));
                user = userService.save(user);
            } else {
                // 更新session_key
                user.setSessionKey(sessionKey);
                user = userService.save(user);
            }
            
            // 3. 使用 TokenUtil 生成 token（替代原来的 wechatService.generateToken）
            String token = tokenUtil.generateToken(openid);
                        
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", Map.of(
                "token", token,
                "user", Map.of(
                    "id", user.getId(),
                    "nickname", user.getNickname() != null ? user.getNickname() : "动漫爱好者",
                    "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""
                )
            ));
                        
            log.info("用户登录成功，userId: {}, token: {}", user.getId(), token);
            
        } catch (Exception e) {
            log.error("登录失败", e);
            response.put("code", 500);
            response.put("message", "登录失败：" + e.getMessage());
        }
        return response;
    }
    
    /**
     * 解密用户信息
     */
    @PostMapping("/decrypt")
    public Map<String, Object> decryptUserInfo(@RequestBody Map<String, String> params) {
        Map<String, Object> response = new HashMap<>();
        
        String encryptedData = params.get("encryptedData");
        String iv = params.get("iv");
        String sessionKey = params.get("sessionKey");
        
        try {
            Map<String, Object> userInfo = wechatService.decryptUserInfo(
                encryptedData, iv, sessionKey);
            
            response.put("code", 200);
            response.put("data", userInfo);
            
        } catch (Exception e) {
            log.error("解密失败", e);
            response.put("code", 500);
            response.put("message", "解密失败");
        }
        return response;
    }
    
    /**
     * 更新用户信息
     */
    @PostMapping("/update-userinfo")
    public Map<String, Object> updateUserInfo(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            User user = userService.findById(userId);
            
            if (user != null) {
                if (params.containsKey("nickname")) {
                    user.setNickname(params.get("nickname").toString());
                }
                if (params.containsKey("avatarUrl")) {
                    user.setAvatarUrl(params.get("avatarUrl").toString());
                }
                if (params.containsKey("gender")) {
                    user.setGender(Integer.parseInt(params.get("gender").toString()));
                }
                
                user = userService.save(user);
                
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", user);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
            }
            
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            response.put("code", 500);
            response.put("message", "更新失败");
        }
        
        return response;
    }

    /**
     * 验证 Token（检查登录状态）
     */
    @GetMapping("/check-token")
    public Map<String, Object> checkToken(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = extractToken(authorization);
            String openid = tokenUtil.validateToken(token);
            
            if (openid != null) {
                User user = userService.findByOpenid(openid);
                response.put("code", 200);
                response.put("message", "token 有效");
                response.put("data", Map.of(
                        "valid", true,
                        "user", user != null ? Map.of(
                                "id", user.getId(),
                                "nickname", user.getNickname(),
                                "avatarUrl", user.getAvatarUrl()
                        ) : null
                ));
            } else {
                response.put("code", 401);
                response.put("message", "token 无效或已过期");
                response.put("data", Map.of("valid", false));
            }
            
        } catch (Exception e) {
            log.error("验证 token 失败", e);
            response.put("code", 500);
            response.put("message", "验证失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 退出登录（使 token 失效）
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String token = extractToken(authorization);
            tokenUtil.invalidateToken(token);
            
            response.put("code", 200);
            response.put("message", "退出成功");
            
        } catch (Exception e) {
            log.error("退出登录失败", e);
            response.put("code", 500);
            response.put("message", "退出失败：" + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 用户订阅管理
     */
    @PostMapping("/subscribe")
    public Map<String, Object> subscribe(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String openid = (String) params.get("openid");
            String templateId = (String) params.get("templateId");
            String scene = (String) params.get("scene"); // new_anime, recommend, etc.
            
            if (openid == null || templateId == null || scene == null) {
                response.put("code", 400);
                response.put("message", "参数缺失");
                return response;
            }
            
            // 保存到数据库（此处简化为日志，实际应存入 User 表 subscriptionStatus 字段）
            log.info("用户 {} 订阅模板 {}（场景：{}）", openid, templateId, scene);
            
            response.put("code", 200);
            response.put("message", "订阅成功");
            
        } catch (Exception e) {
            log.error("订阅失败", e);
            response.put("code", 500);
            response.put("message", "订阅失败：" + e.getMessage());
        }
        return response;
    }
    
    /**
     * 发送订阅消息（管理员或系统触发）
     */
    @PostMapping("/send-subscription")
    public Map<String, Object> sendSubscription(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            String openid = (String) params.get("openid");
            String templateId = (String) params.get("templateId");
            String page = (String) params.get("page");
            Map<String, Object> data = (Map<String, Object>) params.get("data");
            
            if (openid == null || templateId == null || data == null) {
                response.put("code", 400);
                response.put("message", "参数缺失");
                return response;
            }
            
            // 调用微信服务端 API 发送订阅消息
            wechatService.sendSubscriptionMessage(openid, templateId, page, data);
            
            response.put("code", 200);
            response.put("message", "消息已发送");
            
        } catch (Exception e) {
            log.error("发送订阅消息失败", e);
            response.put("code", 500);
            response.put("message", "发送失败：" + e.getMessage());
        }
        return response;
    }
    
    /**
     * 从 Authorization header 中提取 token
     * 格式：Bearer <token>
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
