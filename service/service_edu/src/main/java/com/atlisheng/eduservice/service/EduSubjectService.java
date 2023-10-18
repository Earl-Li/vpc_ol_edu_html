package com.atlisheng.eduservice.service;

import com.atlisheng.eduservice.entity.EduSubject;
import com.atlisheng.eduservice.entity.bo.subject.FirstLevelSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-09-10
 */
public interface EduSubjectService extends IService<EduSubject> {

    /**
     * @param file
     * @param eduSubjectService
     * @描述 通过excel表格实现添加课程分类
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/11
     * @since 1.0.0
     */
    void saveSubject(MultipartFile file,EduSubjectService eduSubjectService);

    /**
     * @return {@link List }<{@link FirstLevelSubject }>
     * @描述 课程列表分类树形结构
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/11
     * @since 1.0.0
     */
    List<FirstLevelSubject> getAllSubject();
}
