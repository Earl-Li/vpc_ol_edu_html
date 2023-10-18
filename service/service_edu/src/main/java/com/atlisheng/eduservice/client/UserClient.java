package com.atlisheng.eduservice.client;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name="service-user",fallback = UserClientImpl.class)
public interface UserClient {
    //根据用户id获取用户信息
    @GetMapping("/eduuser/ucenter/getUserByUserId/{userId}")
    public ResponseData getLoginInfo(@PathVariable("userId") String userId);
}
