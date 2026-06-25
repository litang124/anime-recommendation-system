package com.anime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 移除了 @MapperScan 注解，因为用的是JPA
@SpringBootApplication
@EnableScheduling  // 启用定时任务
public class AnimeRecommendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnimeRecommendApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("启动成功！");
        System.out.println("========================================");
        System.out.println("API地址：http://localhost:8080/api");
        System.out.println("测试接口：http://localhost:8080/api/test/hello");
        System.out.println("管理后台：http://localhost:8080/api/admin/index.html");
        System.out.println("   账号：admin / 密码：admin123");
        System.out.println("========================================\n");
    }
}