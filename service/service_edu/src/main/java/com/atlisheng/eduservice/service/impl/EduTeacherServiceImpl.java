package com.atlisheng.eduservice.service.impl;

import com.atlisheng.eduservice.entity.EduTeacher;
import com.atlisheng.eduservice.mapper.EduTeacherMapper;
import com.atlisheng.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *  EduTeacherMapper继承了BaseMapper<EduTeacher>,并且注入到ServiceImpl的baseMapper属性中，然后被EduTeacherServiceImpl继承
 * @author Earl
 * @since 2023-08-27
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {

    /**
     * @return {@link List }<{@link EduTeacher }>
     * @描述 查询最高资历的四位讲师
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @Override
    public List<EduTeacher> queryPopularTeacher() {
        QueryWrapper<EduTeacher> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("level");
        queryWrapper.last("limit 4");
        List<EduTeacher> teacherList = list(queryWrapper);
        return teacherList;
    }

    /**
     * @param pageParam
     * @return {@link Map }<{@link String }, {@link Object }>
     * @描述 分页查询前台讲师列表，自己写分页组件，不使用element-ui，传给前端的数据多一些
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/08
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> pageFrontTeacherList(Page<EduTeacher> pageParam) {
        QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        baseMapper.selectPage(pageParam, queryWrapper);
        List<EduTeacher> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("curPageTeachers", records);//这个是每页的记录
        map.put("current", current);//这个是当前页码
        map.put("pages", pages);//这个是总页数
        map.put("size", size);//这个是每页的记录条数
        map.put("total", total);//总记录数
        map.put("hasNext", hasNext);//这个是翻页组件的前一页
        map.put("hasPrevious", hasPrevious);//这个是翻页组件的后一页
        return map;
    }
}
