package com.atlisheng.educms.client;

import com.atlisheng.commonutils.response.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("service-edu")
@Component
public interface EduServiceClient {
    /**
     * @return {@link ResponseData }
     * @描述 cms通过Feign远程调用edu服务查询浏览量最高的八个课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @GetMapping("/eduservice/course/queryPopularCourse")
    ResponseData queryPopularCourse();

    /**
     * @return {@link ResponseData }
     * @描述 cms通过Feign远程调用edu服务查询浏览量最高的八个课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @GetMapping("/eduservice/teacher/queryPopularTeacher")
    ResponseData queryPopularTeacher();
}
