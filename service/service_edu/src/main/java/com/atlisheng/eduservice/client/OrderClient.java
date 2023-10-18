package com.atlisheng.eduservice.client;

import com.atlisheng.eduservice.client.impl.OrderClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="service-order",fallback = OrderClientImpl.class)
@Component
public interface OrderClient {
    @GetMapping("/eduorder/order/confirmCourseBuyStatus/{userId}/{courseId}")
    public boolean confirmCourseBuyStatus(@PathVariable String userId, @PathVariable String courseId);
}
