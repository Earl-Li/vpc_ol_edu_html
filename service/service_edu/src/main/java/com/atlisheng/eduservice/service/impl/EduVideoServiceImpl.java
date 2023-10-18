package com.atlisheng.eduservice.service.impl;

import com.atlisheng.eduservice.entity.EduVideo;
import com.atlisheng.eduservice.mapper.EduVideoMapper;
import com.atlisheng.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    /**
     * @param courseId
     * @描述 根据课程id删除小节记录
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/25
     * @since 1.0.0
     */
    @Override
    public boolean removeByCourseId(String courseId) {
        QueryWrapper<EduVideo> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        return remove(queryWrapper);
    }


}
