package com.anime.controller;

import com.anime.entity.User;
import com.anime.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(user.getUsername()) != null) {
                response.put("code", 400);
                response.put("message", "用户名已存在");
                return response;
            }

            User savedUser = userService.save(user);
            response.put("code", 200);
            response.put("message", "注册成功");
            response.put("data", savedUser);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "注册失败：" + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findById(id);
        if (user != null) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", user);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
        }
        return response;
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userService.findAll();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", users);
        response.put("count", users.size());
        return response;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedUser);
        } else {
            response.put("code", 404);
            response.put("message", "用户不存在");
        }
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        userService.deleteById(id);
        response.put("code", 200);
        response.put("message", "删除成功");
        return response;
    }
}