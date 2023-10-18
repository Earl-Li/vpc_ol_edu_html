package com.atlisheng.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

//创建启动类，但是启动类启动时会自动找数据库配置，当前模块不需要操作数据库，只做上传oss功能，不需要操作数据库，可以把数据库的配置添加上
//也可以在注解@SpringBootApplication中用exclude属性让SpringBoot启动时不去加载数据库配置
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {"com.atlisheng"})
@EnableDiscoveryClient
public class OssApplication {
    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class,args);
    }
}
