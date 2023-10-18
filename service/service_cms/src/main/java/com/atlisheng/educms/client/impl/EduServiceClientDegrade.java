package com.atlisheng.educms.client.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.educms.client.EduServiceClient;
import org.springframework.stereotype.Component;

@Component
public class EduServiceClientDegrade implements EduServiceClient {
    @Override
    public ResponseData queryPopularCourse() {
        return ResponseData.responseErrorCall().message("查询最受欢迎课程time out...");
    }

    @Override
    public ResponseData queryPopularTeacher() {
        return ResponseData.responseErrorCall().message("查询最高资历讲师time out...");
    }
}
