package com.anime.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "🎉 动漫推荐系统 API 服务已启动！<br>" +
                "📚 文档：<a href='/swagger-ui.html'>Swagger UI</a><br>" +
                "🩺 健康检查：<a href='/health/check'>/health/check</a><br>" +
                "🔍 测试接口：<a href='/test/hello'>/test/hello</a>";
    }
}