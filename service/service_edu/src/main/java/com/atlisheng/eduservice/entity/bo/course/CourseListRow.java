package com.atlisheng.eduservice.entity.bo.course;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 封装课程列表多表连接查询的所有属性，等课程多表连接查询手写sql时用上，增加对课程简介和课程讲师的展示，删除部分用不到的字段
 * @创建日期 2023/09/24
 * @since 1.0.0
 */
@Data
public class CourseListRow {
    private String id;

    private String teacherId;

    private String subjectId;

    private String subjectParentId;

    private String title;

    private BigDecimal price;

    private Integer courseTotalTime;

    private String cover;

    private Long buyCount;

    private Long viewCount;

    private Long version;

    private String status;

    private Integer isDeleted;

    private Date gmtCreate;

    private Date gmtModified;

    private String description;

    private String teacherName;

}
