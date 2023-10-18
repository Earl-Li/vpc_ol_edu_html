package com.atlisheng.eduservice.entity.bo.course;

import lombok.Data;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 封装课程信息确认的多表连接查询课程信息
 * @创建日期 2023/09/23
 * @since 1.0.0
 */
@Data
public class PublishConfirmCourseInfo {
    private String id;
    private String title;
    private String cover;
    private Integer courseTotalTime;
    private String firstLevelSubject;
    private String secondLevelSubject;
    private String teacherName;
    private String price;
}
