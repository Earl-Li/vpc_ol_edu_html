package com.atlisheng.eduorder.client;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(name="service-user",fallback = UserClientImpl.class)
public interface UserClient {
    //根据课程id查询课程信息
    @PostMapping("/eduuser/ucenter/getUserByUserId/{userId}")
    public ResponseData getLoginInfo(@PathVariable("userId") String courseId);
}
