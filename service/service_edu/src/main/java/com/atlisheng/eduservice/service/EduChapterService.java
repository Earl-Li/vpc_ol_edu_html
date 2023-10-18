package com.atlisheng.eduservice.service;

import com.atlisheng.eduservice.entity.EduChapter;
import com.atlisheng.eduservice.entity.bo.chapter.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
public interface EduChapterService extends IService<EduChapter> {

    /**
     * @return {@link List }<{@link Chapter }>
     * @描述 通过课程ID获取课程所有章节信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/20
     * @since 1.0.0
     */
    List<Chapter> getChaptersByCourseId(String courseId);

    /**
     * @param chapterId
     * @return boolean
     * @描述 根据ID删除课程章节
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/22
     * @since 1.0.0
     */
    boolean deleteChapter(String chapterId);

    /**
     * @param courseId
     * @return boolean
     * @描述 根据课程id删除课程章节
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/25
     * @since 1.0.0
     */
    boolean removeByCourseId(String courseId);
}
