package com.atlisheng.vod.utils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantProperties implements InitializingBean {
    @Value("${aliyun.vod.file.keyid}")
    private String accessKeyId;
    @Value("${aliyun.vod.file.keysecret}")
    private String accessKeySecret;

    //定义公开静态常量,这样可以避免获取上述赋值的注解而进行变更，不安全
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;

    @Override
    public void afterPropertiesSet() throws Exception {
        ACCESS_KEY_ID=accessKeyId;
        ACCESS_KEY_SECRET=accessKeySecret;
    }
}
