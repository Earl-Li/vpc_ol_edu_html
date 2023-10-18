package com.atlisheng.edustatistics.client.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.edustatistics.client.UserClient;
import com.atlisheng.servicebase.exceptions.CustomException;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public ResponseData queryRegisterCountDaily(String day) {
        throw new CustomException(20002,"统计用户注册数据 time out...");
    }
}
