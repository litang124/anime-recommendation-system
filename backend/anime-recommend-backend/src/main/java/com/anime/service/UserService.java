package com.anime.service;

import com.anime.entity.User;
import java.util.List;

public interface UserService {
    User save(User user);
    User findById(Long id);
    User findByUsername(String username);
    User findByOpenid(String openid);
    List<User> findAll();
    void deleteById(Long id);
    User updateUser(Long id, User user);
}