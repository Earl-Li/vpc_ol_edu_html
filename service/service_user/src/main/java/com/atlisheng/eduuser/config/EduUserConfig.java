package com.atlisheng.eduuser.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EduUserConfig {

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

}


