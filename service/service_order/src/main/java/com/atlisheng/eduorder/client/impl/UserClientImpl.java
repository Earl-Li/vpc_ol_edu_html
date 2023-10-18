package com.atlisheng.eduorder.client.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.UserClient;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public ResponseData getLoginInfo(String courseId) {
        return ResponseData.responseErrorCall().message("获取用户信息 time out...");
    }
}
