package com.anime.controller.admin;

import com.anime.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 管理员登录控制器
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminLoginController {

    private final TokenUtil tokenUtil;
    
    // 简单的管理员账户配置（实际项目应从数据库读取）
    private static final Map<String, String> ADMIN_ACCOUNTS = Map.of(
        "admin", "admin123"
    );

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        log.info("收到管理员登录请求: {}", loginData);
        
        Map<String, Object> response = new HashMap<>();
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        if (username == null || password == null) {
            log.warn("登录失败：用户名或密码为空");
            response.put("code", 400);
            response.put("message", "用户名和密码不能为空");
            return response;
        }
        
        // 验证管理员账户
        String storedPassword = ADMIN_ACCOUNTS.get(username);
        if (storedPassword == null || !storedPassword.equals(password)) {
            log.warn("登录失败：用户名或密码错误, username={}", username);
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            return response;
        }
        
        // 生成管理员token（使用特殊openid标识）
        String adminOpenid = "admin_" + username;
        String token = tokenUtil.generateToken(adminOpenid);
        
        log.info("管理员登录成功: {}", username);
        
        response.put("code", 200);
        response.put("message", "登录成功");
        response.put("data", Map.of(
            "token", token,
            "username", username,
            "isAdmin", true
        ));
        
        return response;
    }
    
    /**
     * 验证管理员Token
     */
    @GetMapping("/verify")
    public Map<String, Object> verify(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> response = new HashMap<>();
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            response.put("code", 401);
            response.put("message", "未提供Token");
            return response;
        }
        
        String token = authorization.substring(7);
        String openid = tokenUtil.validateToken(token);
        
        if (openid == null) {
            response.put("code", 401);
            response.put("message", "Token无效或已过期");
            return response;
        }
        
        boolean isAdmin = openid.startsWith("admin_");
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", Map.of(
            "openid", openid,
            "isAdmin", isAdmin
        ));
        
        return response;
    }
}
