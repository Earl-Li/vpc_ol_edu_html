package com.atlisheng.edusms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("com.atlisheng")
public class SmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmsApplication.class ,args);
    }
}
