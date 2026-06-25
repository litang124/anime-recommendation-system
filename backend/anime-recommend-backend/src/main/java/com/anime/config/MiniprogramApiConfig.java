package com.anime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 微信小程序专用API文档配置
 */
@Configuration
public class MiniprogramApiConfig {
    
    @Bean
    public Docket miniprogramApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("微信小程序API")
                .apiInfo(miniprogramApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.anime.controller"))
                .paths(PathSelectors.ant("/wechat/**")
                        .or(PathSelectors.ant("/recommend/**"))
                        .or(PathSelectors.ant("/scene/**")))
                .build();
    }
    
    private ApiInfo miniprogramApiInfo() {
        return new ApiInfoBuilder()
                .title("动漫推荐微信小程序API")
                .description("小程序专用接口文档\n\n" +
                        "包含以下模块：\n" +
                        "1. 微信登录与用户管理\n" +
                        "2. 协同过滤推荐系统\n" +
                        "3. 名场面识别（差异化功能）\n" +
                        "4. 订阅消息推送")
                .version("1.0.0")
                .contact(new Contact(
                        "动漫推荐团队",
                        "https://github.com/your-repo",
                        "anime@example.com"
                ))
                .build();
    }
}
