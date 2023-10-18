package com.atlisheng.edusms.service;

import java.util.Map;

public interface SmsService {

    /**
     * @param phoneNumber
     * @param param
     * @return boolean
     * @描述 阿里云发送短信服务
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    boolean sendMessage(String phoneNumber, Map<String, Object> param);
}
