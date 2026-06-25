package com.anime.aspect;

import com.anime.annotation.AdminRequired;
import com.anime.entity.AdminOperationLog;
import com.anime.service.AdminOperationLogService;
import com.anime.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员权限验证切面
 */
// @Aspect
// @Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuthAspect {

    private final TokenUtil tokenUtil;
    private final AdminOperationLogService adminOperationLogService;

    @Around("@annotation(adminRequired)")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint, AdminRequired adminRequired) throws Throwable {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes == null) {
            return createErrorResponse("无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        long startTime = System.currentTimeMillis();
        
        // 1. 从 Header 中获取 token
        String authorization = request.getHeader("Authorization");
        String token = extractToken(authorization);
        
        // 2. 验证 token
        String openid = tokenUtil.validateToken(token);
        if (openid == null) {
            log.warn("Token 无效或已过期：URI={}", uri);
            return createErrorResponse("未授权访问，请先登录");
        }
        
        // 3. 检查是否是管理员
        // 方式1: openid以"admin_"开头表示管理员登录
        // 方式2: 请求参数中带isAdmin=true
        boolean isAdmin = openid.startsWith("admin_") || 
                         (request.getParameter("isAdmin") != null && 
                          Boolean.parseBoolean(request.getParameter("isAdmin")));
        
        int level = 0;
        try {
            String adminLevelParam = request.getParameter("adminLevel");
            if (adminLevelParam != null) {
                level = Integer.parseInt(adminLevelParam);
            }
        } catch (NumberFormatException e) {
            log.warn("管理员级别参数格式错误：{}", request.getParameter("adminLevel"));
        }
        
        // 获取管理员信息
        Long adminId = null;
        String username = "unknown";
        try {
            // TODO: 从 Token 或数据库中获取管理员 ID 和用户名
            // 这里暂时使用 openid 作为标识
            adminId = -1L; // 临时值
            username = "admin_" + openid.substring(0, Math.min(8, openid.length()));
        } catch (Exception e) {
            log.error("获取管理员信息失败", e);
        }
        
        if (!isAdmin) {
            log.warn("非管理员尝试访问受限接口：URI={}, openid={}", uri, openid);
            saveOperationLog(adminId, username, "UNAUTHORIZED", "非管理员访问", 
                           request.getMethod(), uri, getParams(request), getIpAddress(request), 
                           System.currentTimeMillis() - startTime, false, "非管理员");
            return createErrorResponse("需要管理员权限");
        }
        
        if (level < adminRequired.level()) {
            log.warn("管理员权限不足：URI={}, 当前级别={}, 需要级别={}", 
                    uri, level, adminRequired.level());
            saveOperationLog(adminId, username, "PERMISSION_DENIED", "权限不足", 
                           request.getMethod(), uri, getParams(request), getIpAddress(request), 
                           System.currentTimeMillis() - startTime, false, "权限不足");
            return createErrorResponse("管理员权限不足");
        }
        
        log.info("管理员权限验证通过：URI={}, openid={}, 级别={}", uri, openid, level);
        
        // 执行目标方法并记录日志
        try {
            Object result = joinPoint.proceed();
            
            // 记录成功日志
            saveOperationLog(
                adminId, 
                username, 
                adminRequired.operationType(), 
                adminRequired.description(),
                request.getMethod(), 
                uri, 
                getParams(request), 
                getIpAddress(request), 
                System.currentTimeMillis() - startTime, 
                true, 
                null
            );
            
            return result;
            
        } catch (Throwable e) {
            // 记录失败日志
            saveOperationLog(
                adminId, 
                username, 
                adminRequired.operationType(), 
                adminRequired.description(),
                request.getMethod(), 
                uri, 
                getParams(request), 
                getIpAddress(request), 
                System.currentTimeMillis() - startTime, 
                false, 
                e.getMessage()
            );
            throw e;
        }
    }

    /**
     * 从 Authorization header 中提取 token
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 403);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
    
    /**
     * 保存操作日志
     */
    private void saveOperationLog(Long adminId, String username, String operationType, 
                                  String operationDesc, String requestMethod, String requestUrl,
                                  String requestParams, String ipAddress, Long executionTime,
                                  boolean success, String errorMessage) {
        try {
            AdminOperationLog log = new AdminOperationLog();
            log.setAdminId(adminId);
            log.setAdminUsername(username);
            log.setOperationType(operationType);
            log.setOperationDesc(operationDesc);
            log.setRequestMethod(requestMethod);
            log.setRequestUrl(requestUrl);
            log.setRequestParams(requestParams);
            log.setIpAddress(ipAddress);
            log.setExecutionTime(executionTime);
            log.setStatus(success ? 1 : 0);
            log.setErrorMessage(errorMessage);
            
            adminOperationLogService.save(log);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }
    
    /**
     * 获取请求参数
     */
    private String getParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        StringBuilder params = new StringBuilder();
        
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            params.append(entry.getKey()).append("=");
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                params.append(values[0]);
            }
            params.append("&");
        }
        
        return params.length() > 0 ? params.substring(0, params.length() - 1) : "";
    }
    
    /**
     * 获取 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是 IPv6，转换为 IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
