package com.anime.annotation;

import java.lang.annotation.*;

/**
 * 管理员权限注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminRequired {
    /**
     * 需要的最小权限级别
     * 0: 普通管理员
     * 1: 超级管理员
     */
    int level() default 0;
    
    /**
     * 操作类型（用于日志记录）
     */
    String operationType() default "UNKNOWN";
    
    /**
     * 操作描述（用于日志记录）
     */
    String description() default "";
}
