package com.atlisheng.eduservice.client.impl;

import com.atlisheng.eduservice.client.OrderClient;
import com.atlisheng.servicebase.exceptions.CustomException;
import org.springframework.stereotype.Component;

@Component
public class OrderClientImpl implements OrderClient {
    @Override
    public boolean confirmCourseBuyStatus(String userId, String courseId) {
        throw new CustomException(20002,"获取课程购买状态服务time out...");
    }
}
