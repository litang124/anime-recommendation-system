package com.anime.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 管理员操作日志实体
 */
@Data
@Entity
@Table(name = "admin_operation_log")
public class AdminOperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 管理员用户 ID
     */
    @Column(name = "admin_id", nullable = false)
    private Long adminId;
    
    /**
     * 管理员用户名
     */
    @Column(name = "admin_username")
    private String adminUsername;
    
    /**
     * 操作类型
     */
    @Column(name = "operation_type", nullable = false)
    private String operationType;
    
    /**
     * 操作描述
     */
    @Column(name = "operation_desc")
    private String operationDesc;
    
    /**
     * 请求方法
     */
    @Column(name = "request_method")
    private String requestMethod;
    
    /**
     * 请求 URL
     */
    @Column(name = "request_url")
    private String requestUrl;
    
    /**
     * 请求参数
     */
    @Column(columnDefinition = "TEXT")
    private String requestParams;
    
    /**
     * IP 地址
     */
    @Column(name = "ip_address")
    private String ipAddress;
    
    /**
     * 执行时间（毫秒）
     */
    @Column(name = "execution_time")
    private Long executionTime;
    
    /**
     * 操作状态：0-失败，1-成功
     */
    @Column(name = "status")
    private Integer status = 1;
    
    /**
     * 错误信息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    /**
     * 操作时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
