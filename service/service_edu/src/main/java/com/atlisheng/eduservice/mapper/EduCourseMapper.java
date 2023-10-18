package com.atlisheng.eduservice.mapper;

import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.bo.course.FrontCourseInfo;
import com.atlisheng.eduservice.entity.bo.course.PublishConfirmCourseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
public interface EduCourseMapper extends BaseMapper<EduCourse> {
    /**
     * @param courseId
     * @return {@link PublishConfirmCourseInfo }
     * @描述 多表联查课程发布确认信息封装成PublishConfirmCourseInfo响应前端
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/23
     * @since 1.0.0
     */
    public PublishConfirmCourseInfo getPublishConfirmCourseInfo(String courseId);

    /**
     * @param courseId
     * @return {@link FrontCourseInfo }
     * @描述 根据课程id查询前台课程信息【包含讲师信息，多表连接查询，需要手写sql】
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    FrontCourseInfo getFrontCourseInfoByCourseId(String courseId);
}
