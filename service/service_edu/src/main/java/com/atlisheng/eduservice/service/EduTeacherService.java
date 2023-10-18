package com.atlisheng.eduservice.service;

import com.atlisheng.eduservice.entity.EduTeacher;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-08-27
 */
public interface EduTeacherService extends IService<EduTeacher> {

    /**
     * @return {@link List }<{@link EduTeacher }>
     * @描述 查询最有资质的前四条名师
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    List<EduTeacher> queryPopularTeacher();

    /**
     * @param pageParam
     * @return {@link Map }<{@link String }, {@link Object }>
     * @描述 分页查询前台讲师列表
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/08
     * @since 1.0.0
     */
    Map<String, Object> pageFrontTeacherList(Page<EduTeacher> pageParam);
}
