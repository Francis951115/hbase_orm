/*
 * Copyright (c) 2019. zhangbohan.dell@gmail.com ,All Rights Reserved
 *
 */

package xyz.zhangbohan.hive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * file encoding: utf-8
 * Function : swagger2的配置信息
 * Create : 2018/8/1 19:59
 *
 * @author : zhangbohan.dell@gmail.com
 * @version : 1.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo())
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage("xyz.zhangbohan.hive"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("Spring Boot中使用Swagger2构建RESTful APIs")
                //介绍
                .description("更多Spring Boot相关文章请关注官方网站")
                //创建人
                .contact(new Contact("zhangbohan", "http://zhangbohan.xyz", "mir2285@outlook.com"))
                .termsOfServiceUrl("http://zhangbohan.xyz")
                //版本号
                .version("1.0")
                .build();
    }
}
