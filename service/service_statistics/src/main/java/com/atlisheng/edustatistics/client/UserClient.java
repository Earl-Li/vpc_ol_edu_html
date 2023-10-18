package com.atlisheng.edustatistics.client;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.edustatistics.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name="service-user",fallback = UserClientImpl.class)
public interface UserClient {
    @GetMapping(value = "/eduuser/ucenter/queryRegisterCountDaily/{day}")
    public ResponseData queryRegisterCountDaily(@PathVariable("day") String day);
}
