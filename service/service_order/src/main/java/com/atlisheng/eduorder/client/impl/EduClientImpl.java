package com.atlisheng.eduorder.client.impl;

import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.EduClient;
import com.atlisheng.servicebase.exceptions.CustomException;
import org.springframework.stereotype.Component;

@Component
public class EduClientImpl implements EduClient {

    @Override
    public CourseInfoForm queryCourseInfoById(String courseId) {
        throw new CustomException(20002,"获取课程信息 time out...");
    }

    @Override
    public ResponseData queryTeacherNameById(String teacherId) {
        return ResponseData.responseErrorCall().message("获取讲师名字 time out...");
    }

    @Override
    public ResponseData updateCourseBuyCount(String courseId) {
        return ResponseData.responseErrorCall().message("更新课程购买数量 time out...");
    }
}
