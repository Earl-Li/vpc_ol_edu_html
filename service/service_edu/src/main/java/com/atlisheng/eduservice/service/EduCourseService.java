package com.atlisheng.eduservice.service;

import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.bo.course.FrontCourseInfo;
import com.atlisheng.eduservice.entity.bo.course.PublishConfirmCourseInfo;
import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.entity.vo.FrontCourseQueryFactor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
public interface EduCourseService extends IService<EduCourse> {

    EduCourse saveCourseInfo(CourseInfoForm courseInfoForm);

    /**
     * @return {@link CourseInfoForm }
     * @描述 根据课程Id获取课程信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/20
     * @since 1.0.0
     */
    CourseInfoForm getCourseInfoById(String id);

    /**
     * @param courseInfoForm
     * @描述 根据课程更新信息更新数据库记录
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/20
     * @since 1.0.0
     */
    void updateCourseInfo(CourseInfoForm courseInfoForm);

    /**
     * @return {@link PublishConfirmCourseInfo }
     * @描述 根据课程ID查询课程发布确认信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/23
     * @since 1.0.0
     */
    PublishConfirmCourseInfo getPublishConfirmCourseInfo(String courseId);

    /**
     * @param courseId
     * @描述 根据课程id删除课程、章节、小节、视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/30
     * @since 1.0.0
     */
    void removeCourseByCourseId(String courseId);

    /**
     * @return {@link List }<{@link EduCourse }>
     * @描述 查询浏览量最多的八门课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    List<EduCourse> queryPopularCourse();

    /**
     * @return {@link List }<{@link EduCourse }>
     * @描述 根据讲师id获取讲师对应课程的相关信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/09
     * @since 1.0.0
     */
    List<EduCourse> getCourseInfoByTeacherId(String teacherId);

    /**
     * @param pageParam
     * @param courseQueryFactor
     * @return {@link Map }<{@link String }, {@link Object }>
     * @描述 多条件分页查询课程列表
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/09
     * @since 1.0.0
     */
    Map<String, Object> pageFactorCourse(Page<EduCourse> pageParam, FrontCourseQueryFactor courseQueryFactor);

    /**
     * @param courseId
     * @描述 每次获取课程信息展示在前台都会增加课程浏览数
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    boolean updateCourseViewCount(String courseId);

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

    /**
     * @return boolean
     * @描述 根据课程id更新课程购买数量
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     * @param courseId
     */
    boolean updateCourseBuyCount(String courseId);
}
