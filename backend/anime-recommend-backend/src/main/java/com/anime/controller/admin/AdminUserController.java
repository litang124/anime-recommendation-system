package com.anime.controller.admin;

import com.anime.annotation.AdminRequired;
import com.anime.entity.User;
import com.anime.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理（后台）
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @AdminRequired(operationType = "USER_LIST", description = "查询用户列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> allUsers = userService.findAll();
            
            // 手动分页
            int start = page * size;
            int end = Math.min(start + size, allUsers.size());
            List<User> pageUsers = allUsers.subList(start, end);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", pageUsers);
            response.put("total", allUsers.size());
            response.put("pages", (int) Math.ceil((double) allUsers.size() / size));
            response.put("currentPage", page);
            
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @AdminRequired(operationType = "USER_DETAIL", description = "查询用户详情")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = userService.findById(id);
            
            if (user != null) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", user);
            } else {
                response.put("code", 404);
                response.put("message", "用户不存在");
            }
            
        } catch (Exception e) {
            log.error("获取用户详情失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @AdminRequired(operationType = "USER_UPDATE", description = "更新用户信息")
    public Map<String, Object> update(
            @PathVariable Long id,
            @RequestBody User user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User existing = userService.findById(id);
            if (existing == null) {
                response.put("code", 404);
                response.put("message", "用户不存在");
                return response;
            }
            
            // 更新字段
            if (user.getNickname() != null) existing.setNickname(user.getNickname());
            if (user.getAvatarUrl() != null) existing.setAvatarUrl(user.getAvatarUrl());
            if (user.getEmail() != null) existing.setEmail(user.getEmail());
            if (user.getGender() != null) existing.setGender(user.getGender());
            
            User updated = userService.save(existing);
            
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updated);
            
        } catch (Exception e) {
            log.error("更新用户失败", e);
            response.put("code", 500);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @AdminRequired(level = 1, operationType = "USER_DELETE", description = "删除用户")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            userService.deleteById(id);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            
        } catch (Exception e) {
            log.error("删除用户失败", e);
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @AdminRequired(operationType = "USER_SEARCH", description = "搜索用户")
    public Map<String, Object> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> allUsers = userService.findAll();
            
            // 模糊搜索用户名或昵称
            List<User> searchResults = allUsers.stream()
                    .filter(u -> (u.getUsername() != null && u.getUsername().contains(keyword)) ||
                                 (u.getNickname() != null && u.getNickname().contains(keyword)))
                    .toList();
            
            // 手动分页
            int start = page * size;
            int end = Math.min(start + size, searchResults.size());
            List<User> pageResults = searchResults.subList(start, end);
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", pageResults);
            response.put("total", searchResults.size());
            response.put("pages", (int) Math.ceil((double) searchResults.size() / size));
            response.put("currentPage", page);
            
        } catch (Exception e) {
            log.error("搜索用户失败", e);
            response.put("code", 500);
            response.put("message", "搜索失败：" + e.getMessage());
        }
        
        return response;
    }

    /**
     * 用户统计
     */
    @GetMapping("/statistics")
    @AdminRequired(operationType = "USER_STATISTICS", description = "用户统计")
    public Map<String, Object> statistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<User> allUsers = userService.findAll();
            
            // 总用户数
            long totalCount = allUsers.size();
            
            // 新增用户（最近 7 天）
            long newUsersCount = allUsers.stream()
                    .filter(u -> u.getCreateTime() != null)
                    .filter(u -> u.getCreateTime().isAfter(java.time.LocalDateTime.now().minusDays(7)))
                    .count();
            
            // 活跃用户（有观看记录的，简单统计）
            long activeUsers = allUsers.stream()
                    .filter(u -> u.getUpdateTime() != null)
                    .filter(u -> u.getUpdateTime().isAfter(java.time.LocalDateTime.now().minusDays(7)))
                    .count();
            
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", Map.of(
                    "total", totalCount,
                    "newUsersLast7Days", newUsersCount,
                    "activeUsersLast7Days", activeUsers
            ));
            
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            response.put("code", 500);
            response.put("message", "获取失败：" + e.getMessage());
        }
        
        return response;
    }
}
