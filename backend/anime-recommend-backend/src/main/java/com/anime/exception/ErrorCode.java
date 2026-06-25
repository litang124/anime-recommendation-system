package com.anime.exception;

import lombok.Getter;

/**
 * 业务异常码枚举
 */
@Getter
public enum ErrorCode {

    // ========== 通用错误 (1000-1999) ==========
    SUCCESS(200, "success"),
    
    SYSTEM_ERROR(1000, "系统繁忙，请稍后再试"),
    PARAM_ERROR(1001, "参数错误"),
    DATA_NOT_FOUND(1004, "数据不存在"),
    DATA_ALREADY_EXISTS(1005, "数据已存在"),
    
    // ========== 认证授权错误 (2000-2999) ==========
    UNAUTHORIZED(2000, "未授权访问，请先登录"),
    TOKEN_INVALID(2001, "Token 无效或已过期"),
    TOKEN_EXPIRED(2002, "Token 已过期"),
    PERMISSION_DENIED(2003, "权限不足"),
    ADMIN_REQUIRED(2004, "需要管理员权限"),
    
    // ========== 用户相关错误 (3000-3999) ==========
    USER_NOT_FOUND(3000, "用户不存在"),
    USER_ALREADY_EXISTS(3001, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(3002, "用户名或密码错误"),
    WECHAT_LOGIN_FAILED(3003, "微信登录失败"),
    
    // ========== 动漫相关错误 (4000-4999) ==========
    ANIME_NOT_FOUND(4000, "动漫不存在"),
    ANIME_ALREADY_EXISTS(4001, "动漫已存在"),
    
    // ========== 评论相关错误 (5000-5999) ==========
    REVIEW_NOT_FOUND(5000, "评论不存在"),
    REVIEW_CONTENT_EMPTY(5001, "评论内容不能为空"),
    REVIEW_RATING_INVALID(5002, "评分不合法，范围 1-5"),
    
    // ========== 收藏相关错误 (6000-6999) ==========
    FAVORITE_NOT_FOUND(6000, "收藏记录不存在"),
    FAVORITE_ALREADY_EXISTS(6001, "已收藏"),
    
    // ========== 日程相关错误 (7000-7999) ==========
    SCHEDULE_NOT_FOUND(7000, "日程不存在"),
    
    // ========== 推荐相关错误 (8000-8999) ==========
    RECOMMENDATION_NOT_FOUND(8000, "暂无推荐");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
