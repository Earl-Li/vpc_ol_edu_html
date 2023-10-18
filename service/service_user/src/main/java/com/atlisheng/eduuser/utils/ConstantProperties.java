package com.atlisheng.eduuser.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 这个类使用value注解读取配置文件内容，方便使用;InitializingBean可以在SpringBoot加载后执行afterPropertiesSet方法
 * @创建日期 2023/09/04
 * @since 1.0.0
 */
@Component
public class ConstantProperties implements InitializingBean {

    @Value("${vpc.edu.user.default-avatar}")
    private String defaultAvatar;
    @Value("${wx.open.app_id}")
    private String appId;
    @Value("${wx.open.app_secret}")
    private String appSecret;
    @Value("${wx.open.redirect_url}")
    private String redirectUrl;


    //定义公开的静态常量
    public static String DEFAULT_AVATAR;
    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        DEFAULT_AVATAR=defaultAvatar;
        WX_OPEN_APP_ID=appId;
        WX_OPEN_APP_SECRET=appSecret;
        WX_OPEN_REDIRECT_URL=redirectUrl;
    }
}
