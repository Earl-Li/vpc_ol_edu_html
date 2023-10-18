package com.atlisheng.eduservice.service.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.VodClient;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.EduCourseDescription;
import com.atlisheng.eduservice.entity.EduVideo;
import com.atlisheng.eduservice.entity.bo.course.FrontCourseInfo;
import com.atlisheng.eduservice.entity.bo.course.PublishConfirmCourseInfo;
import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.entity.vo.FrontCourseQueryFactor;
import com.atlisheng.eduservice.mapper.EduCourseMapper;
import com.atlisheng.eduservice.service.EduChapterService;
import com.atlisheng.eduservice.service.EduCourseDescriptionService;
import com.atlisheng.eduservice.service.EduCourseService;
import com.atlisheng.eduservice.service.EduVideoService;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;
    @Autowired
    private EduChapterService eduChapterService;
    @Autowired
    private EduVideoService eduVideoService;
    @Autowired
    private VodClient vodClient;

    /**
     * @param courseInfoForm
     * @描述 由于课程表和课程信息表是一对一的关系，直接拿课程表的id作为课程信息表的id
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/12
     * @since 1.0.0
     */
    @Override
    public EduCourse saveCourseInfo(CourseInfoForm courseInfoForm) {
        //向课程表添加课程基本信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm,eduCourse);
        //注意一下baseMapper.insert返回的是插入记录条数，但是service中封装的返回值是当插入记录条数大于等于1且不为null就返回true
        //课程表添加信息不成功就抛出异常，这儿似乎不需要事务，因为添加失败了抛异常后续也不会执行了,但是后续课程简介仍然可能添加失败，最好还是加上事务
        if (!save(eduCourse)){
            throw new CustomException(20001,"添加课程信息失败");
        }
        //向课程简介表添加课程简介，同时注意将课程表的id设置为可成信息表对应的id
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(eduCourse.getId()).setDescription(courseInfoForm.getDescription());
        eduCourseDescriptionService.save(eduCourseDescription);
        return eduCourse;
    }

    /**
     * @param id
     * @return {@link CourseInfoForm }
     * @描述 通过课程id获取对应课程信息封装成courseInfoForm对象进行返回，意在展示课程基本信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/20
     * @since 1.0.0
     */
    @Override
    public CourseInfoForm getCourseInfoById(String id) {
        //准备封装返回查询数据的对象
        CourseInfoForm courseInfoForm=new CourseInfoForm();
        //根据课程id查询课程表,String类实现了可序列化接口，传参的id被封装成课序列化多态，这里仍然可以直接使用String类型的id
        EduCourse eduCourse = getById(id);
        BeanUtils.copyProperties(eduCourse,courseInfoForm);
        EduCourseDescription eduCourseDescription = eduCourseDescriptionService.getById(id);
        courseInfoForm.setDescription(eduCourseDescription.getDescription());
        return courseInfoForm;
    }

    /**
     * @param courseInfoForm
     * @描述 更新课程信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/20
     * @since 1.0.0
     */
    @Override
    public void updateCourseInfo(CourseInfoForm courseInfoForm) {
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoForm,eduCourse);
        if (!updateById(eduCourse)){
            throw new CustomException(20001,"课程信息保存失败");
        }
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoForm.getId()).setDescription(courseInfoForm.getDescription());
        if(!eduCourseDescriptionService.updateById(eduCourseDescription)){
            throw new CustomException(20001,"课程详情信息保存失败");
        }
    }

    /**
     * @param courseId
     * @return {@link PublishConfirmCourseInfo }
     * @描述 根据课程ID查询课程发布确认信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/23
     * @since 1.0.0
     */
    @Override
    public PublishConfirmCourseInfo getPublishConfirmCourseInfo(String courseId) {
        return baseMapper.getPublishConfirmCourseInfo(courseId);
    }

    /**
     * @param courseId
     * @描述 根据课程id删除课程信息，包括小节、章节、课程描述、课程本身
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/25
     * @since 1.0.0
     */
    @Override
    public void removeCourseByCourseId(String courseId) {
        //获取该课程下的所有视频ID
        QueryWrapper<EduVideo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        queryWrapper.select("video_source_id");
        List<EduVideo> eduVideoList = eduVideoService.list(queryWrapper);
        List<String> videoIdList=new ArrayList<>();
        eduVideoList.forEach(eduVideo -> {
            String videoSourceId = eduVideo.getVideoSourceId();
            if(!StringUtils.isEmpty(videoSourceId)){
                videoIdList.add(videoSourceId);
            }
        });
        if (videoIdList.size()>0){
            ResponseData responseData = vodClient.removeVodVideoByIds(videoIdList);
            if (!(responseData.getCode()==20000)) {
                throw new CustomException(20001,"删除多个视频服务器挂掉了");
            }
        }
        if(eduVideoService.removeByCourseId(courseId) && eduChapterService.removeByCourseId(courseId) && eduCourseDescriptionService.removeById(courseId) && removeById(courseId)){
            return;
        }
        throw new CustomException(20001,"课程删除失败");
    }

    /**
     * @return {@link List }<{@link EduCourse }>
     * @描述 查询浏览量最多的八门课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @Override
    public List<EduCourse> queryPopularCourse() {
        List<EduCourse> courseList = list(new QueryWrapper<EduCourse>().orderByDesc("view_count").last("limit 8"));
        return courseList;
    }

    /**
     * @param teacherId
     * @return {@link List }<{@link EduCourse }>
     * @描述 根据讲师id查询讲师授课课程列表
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/09
     * @since 1.0.0
     */
    @Override
    public List<EduCourse> getCourseInfoByTeacherId(String teacherId) {
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        //按照最后更新时间倒序排列
        queryWrapper.orderByDesc("gmt_modified");
        List<EduCourse> courses = baseMapper.selectList(queryWrapper);
        return courses;
    }

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
    @Override
    public Map<String, Object> pageFactorCourse(Page<EduCourse> pageParam, FrontCourseQueryFactor courseQueryFactor) {
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        //课程一级分类id
        if (!StringUtils.isEmpty(courseQueryFactor.getSubjectParentId())) {
            queryWrapper.eq("subject_parent_id",
                    courseQueryFactor.getSubjectParentId());
        }
        //课程二级分类id
        if (!StringUtils.isEmpty(courseQueryFactor.getSubjectId())) {
            queryWrapper.eq("subject_id", courseQueryFactor.getSubjectId());
        }
        //按照字段排序，前端传的字符串"1",为1就设置对应的字段排序，前端每次点击排序都会将其他排序初始化
        //售货量
        if (!StringUtils.isEmpty(courseQueryFactor.getBuyCountSort())) {
            queryWrapper.orderByDesc("buy_count");
        }
        //课程创建时间
        if (!StringUtils.isEmpty(courseQueryFactor.getGmtCreateSort())) {
            queryWrapper.orderByDesc("gmt_create");
        }
        //课程价格
        if (!StringUtils.isEmpty(courseQueryFactor.getPriceSort())) {
            queryWrapper.orderByDesc("price");
        }
        baseMapper.selectPage(pageParam, queryWrapper);
        List<EduCourse> courseList = pageParam.getRecords();
        long curPage = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("courses", courseList);
        map.put("curPage", curPage);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        return map;
    }

    /**
     * @param courseId
     * @描述 每次获取课程信息展示在前台都会增加课程浏览数,返回true表示更新成功，返回false表示更新失败
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @Override
    public boolean updateCourseViewCount(String courseId) {
        EduCourse eduCourse = getById(courseId);
        eduCourse.setViewCount(eduCourse.getViewCount()+1);
        return updateById(eduCourse);
    }

    /**
     * @param courseId
     * @return {@link FrontCourseInfo }
     * @描述 根据课程id查询前台课程信息【包含讲师信息，多表连接查询，需要手写sql】
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @Override
    public FrontCourseInfo getFrontCourseInfoByCourseId(String courseId) {
        //更新课程浏览数
        if (! updateCourseViewCount(courseId)){
            throw new CustomException(20001,"课程浏览数更新失败!");
        }
        return baseMapper.getFrontCourseInfoByCourseId(courseId);
    }

    /**
     * @param courseId
     * @return boolean
     * @描述 根据课程id更新课程购买人数供订单服务调用
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @Override
    public boolean updateCourseBuyCount(String courseId) {
        EduCourse eduCourse = getById(courseId);
        eduCourse.setBuyCount(eduCourse.getBuyCount()+1);
        return updateById(eduCourse);
    }
}
