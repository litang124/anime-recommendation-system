package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;          // 用户名
    
    @Column(name = "openid", unique = true)
    private String openid;            // 微信openid
    
    @Column(name = "session_key")
    private String sessionKey;        // 微信session_key

    private String nickname;          // 昵称
    private String avatarUrl;         // 头像
    private String email;             // 邮箱
    private Integer gender = 0;       // 0:未知 1:男 2:女

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    private LocalDateTime updateTime = LocalDateTime.now();
}