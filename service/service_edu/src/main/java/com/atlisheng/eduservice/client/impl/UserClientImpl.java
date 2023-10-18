package com.atlisheng.eduservice.client.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.UserClient;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public ResponseData getLoginInfo(String userId) {
        return ResponseData.responseErrorCall().message("获取用户信息 time out...");
    }
}
