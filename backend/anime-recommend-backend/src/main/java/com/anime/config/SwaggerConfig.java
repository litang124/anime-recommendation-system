package com.anime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.anime.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalResponses(HttpMethod.GET, globalResponseMessages())
                .globalResponses(HttpMethod.POST, globalResponseMessages())
                .globalResponses(HttpMethod.PUT, globalResponseMessages())
                .globalResponses(HttpMethod.DELETE, globalResponseMessages());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("动漫推荐系统 API")
                .description("动漫推荐系统后端接口文档")
                .contact(new Contact("开发团队", "", ""))
                .version("1.0")
                .build();
    }
    
    /**
     * 全局响应消息
     */
    private List<Response> globalResponseMessages() {
        return Arrays.asList(
                new ResponseBuilder()
                        .code("200")
                        .description("成功")
                        .build(),
                new ResponseBuilder()
                        .code("400")
                        .description("请求参数错误")
                        .build(),
                new ResponseBuilder()
                        .code("404")
                        .description("资源不存在")
                        .build(),
                new ResponseBuilder()
                        .code("500")
                        .description("服务器内部错误")
                        .build()
        );
    }
}