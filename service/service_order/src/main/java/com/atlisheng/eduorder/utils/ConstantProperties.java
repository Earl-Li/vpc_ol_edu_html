package com.atlisheng.eduorder.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantProperties implements InitializingBean {
    @Value("${weixin.pay.appid}")
    private String appId;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partner-key}")
    private String partnerKey;

    @Value("${weixin.pay.notify-url}")
    private String notifyUrl;

    //定义公开的静态常量
    public static String APP_ID;
    public static String PARTNER;
    public static String PARTNER_KEY;
    public static String NOTIFY_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        APP_ID=appId;
        PARTNER=partner;
        PARTNER_KEY=partnerKey;
        NOTIFY_URL=notifyUrl;
    }
}
