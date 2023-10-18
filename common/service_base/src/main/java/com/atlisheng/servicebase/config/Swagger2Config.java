package com.atlisheng.servicebase.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2//这个注解的作用不明白，是swagger的注解，估计是使swagger生效的注解
public class Swagger2Config {
    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")//这个名字表示组名，可以随便取
                .apiInfo(webApiInfo())//apiInfo调用webApiInfo()方法设置在线文档的一些信息
                .select()
                //.paths(Predicates.not(PathSelectors.regex("/admin/.*")))//这两行表示如果请求路径中包含这两种格式就不适用swagger对其进行显示
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-VPC课程中心API文档")
                .description("本文档描述了课程中心微服务接口定义")
                .version("1.0")
                .contact(new Contact("Earl", "http://atlisheng.com", "2625074321@qq.com"))
                .build();
    }
}
