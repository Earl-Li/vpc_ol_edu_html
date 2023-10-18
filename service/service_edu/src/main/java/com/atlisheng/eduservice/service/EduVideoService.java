package com.atlisheng.eduservice.service;

import com.atlisheng.eduservice.entity.EduVideo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
public interface EduVideoService extends IService<EduVideo> {

    /**
     * @param courseId
     * @return boolean
     * @描述 根据课程id删除课程小节
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/25
     * @since 1.0.0
     */
    boolean removeByCourseId(String courseId);


}
