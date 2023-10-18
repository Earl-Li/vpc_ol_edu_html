package com.atlisheng.eduorder.client;

import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.impl.EduClientImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name="service-edu",fallback = EduClientImpl.class)
public interface EduClient {
    //根据课程id查询课程信息
    @GetMapping("/eduservice/course/queryCourseInfo/{courseId}")
    public CourseInfoForm queryCourseInfoById(@PathVariable("courseId") String courseId);

    //根据讲师id查询单个讲师
    @GetMapping("/eduservice/teacher/queryTeacherName/{teacherId}")
    public ResponseData queryTeacherNameById(@PathVariable("teacherId") String teacherId);

    //根据课程id更新课程购买数量
    @GetMapping("/eduservice/course/updateCourseBuyCount/{courseId}")
    public ResponseData updateCourseBuyCount(@PathVariable("courseId") String courseId);
}
