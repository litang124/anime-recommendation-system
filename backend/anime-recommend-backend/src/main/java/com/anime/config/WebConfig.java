package com.anime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置
 * 支持小程序和Web前端跨域访问
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许所有来源（小程序、Web等）
                .allowedOriginPatterns("*")
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证（Cookie、Authorization header等）
                .allowCredentials(true)
                // 预检请求的缓存时间（秒）
                .maxAge(3600)
                // 暴露的响应头
                .exposedHeaders("Authorization", "Content-Type", "X-Total-Count");
    }
    
    /**
     * 配置静态资源映射
     * 使管理后台页面可以通过 /admin/** 访问
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 管理后台静态资源
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/static/admin/");
    }
}