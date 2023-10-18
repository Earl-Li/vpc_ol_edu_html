package com.atlisheng.eduservice.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.atlisheng.eduservice.mapper")
public class EduServiceConfig {

    /**
     * @return {@link ISqlInjector }
     * @描述 配置mp逻辑删除插件
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @Bean
    public ISqlInjector iSqlInjector(){
        return new LogicSqlInjector();
    }

    /**
     * @return {@link PaginationInterceptor }
     * @描述 配置mp分页插件
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
