package com.anime.service.impl;

import com.anime.entity.User;
import com.anime.exception.BusinessException;
import com.anime.exception.ErrorCode;
import com.anime.repository.UserRepository;
import com.anime.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> 
            new BusinessException(ErrorCode.USER_NOT_FOUND, "用户 ID=" + id + " 不存在")
        );
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User findByOpenid(String openid) {
        return userRepository.findByOpenid(openid);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        User existingUser = findById(id); // 这里会抛出异常如果用户不存在
        
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(user.getAvatarUrl());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        existingUser.setUpdateTime(LocalDateTime.now());
        return userRepository.save(existingUser);
    }
}