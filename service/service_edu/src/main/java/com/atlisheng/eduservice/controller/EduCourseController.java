package com.atlisheng.eduservice.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.bo.course.PublishConfirmCourseInfo;
import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.entity.vo.CourseQueryFactor;
import com.atlisheng.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
@RestController
@RequestMapping("/eduservice/course")

@Api(description = "课程信息管理")
public class EduCourseController {

    @Autowired
    private EduCourseService eduCourseService;

    @PostMapping("addCourseInfo")
    @ApiOperation("添加课程信息")
    public ResponseData addCourseInfo(@RequestBody CourseInfoForm courseInfoForm){
        EduCourse eduCourse = eduCourseService.saveCourseInfo(courseInfoForm);
        return ResponseData.responseCall().data("courseId",eduCourse.getId());
    }

    @GetMapping("addCourseInfo/{courseId}")
    @ApiOperation("回显课程信息")
    public ResponseData echoCourseInfoById(@ApiParam(name="id",value = "课程ID",required = true) @PathVariable String courseId ){
        CourseInfoForm courseInfo=eduCourseService.getCourseInfoById(courseId);
        return ResponseData.responseCall().data("courseInfo",courseInfo);
    }

    @GetMapping("queryCourseInfo/{courseId}")
    @ApiOperation("订单服务根据课程id获取课程信息")
    public CourseInfoForm queryCourseInfoById(@ApiParam(name="courseId",value = "课程ID",required = true) @PathVariable("courseId") String courseId ){
        CourseInfoForm courseInfo=eduCourseService.getCourseInfoById(courseId);
        return courseInfo;
    }

    @PutMapping("addCourseInfo")
    @ApiOperation("更新课程信息")
    public ResponseData updateCourseInfo(@ApiParam(name="CourseInfoForm",value = "课程更新信息",required = true) @RequestBody CourseInfoForm courseInfoForm){
        eduCourseService.updateCourseInfo(courseInfoForm);
        return ResponseData.responseCall().data("courseId",courseInfoForm.getId());
    }

    @GetMapping("getPublishConfirmCourseInfo/{courseId}")
    @ApiOperation("查询课程发布确认信息")
    public ResponseData getPublishConfirmCourseInfo(@ApiParam(name="CourseId",value = "课程ID",required = true) @PathVariable String courseId){
        PublishConfirmCourseInfo publishConfirmCourseInfo=eduCourseService.getPublishConfirmCourseInfo(courseId);
        return ResponseData.responseCall().data("publishConfirmCourseInfo",publishConfirmCourseInfo);
    }

    @PutMapping("publishCourse/{courseId}")
    @ApiOperation("发布课程信息")
    public ResponseData publishCourse(@ApiParam(name="CourseId",value = "课程ID",required = true) @PathVariable String courseId){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(courseId).setStatus("Normal");
        return eduCourseService.updateById(eduCourse)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    /**
     * @param current
     * @param limit
     * @param courseQueryFactor
     * @return {@link ResponseData }
     * @描述 课程多条件组合带分页查询
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/24
     * @since 1.0.0
     */
    //TODO 手写sql实现课程多条件组合带分页多表连接查询，关注QueryWrapper的多表连接查询
    @PostMapping("pageFactorCourse/{current}/{limit}")
    @ApiOperation(value = "课程多条件组合带分页查询")
    public ResponseData findFactorCoursePaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                                @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit,
                                                @ApiParam(name = "courseQueryFactor",value = "讲师筛选条件") @RequestBody(required = false) CourseQueryFactor courseQueryFactor){//@RequestBody将json数据封装到对应的对象中
        Page<EduCourse> coursePage = new Page<>(current,limit);
        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        String title = courseQueryFactor.getTitle();
        String teacherId = courseQueryFactor.getTeacherId();
        String subjectParentId = courseQueryFactor.getSubjectParentId();
        String subjectId = courseQueryFactor.getSubjectId();
        String status = courseQueryFactor.getStatus();
        queryWrapper.orderByDesc("gmt_Create");
        if (!StringUtils.isEmpty(title)){
            queryWrapper.like("title",title);
        }
        if (!StringUtils.isEmpty(teacherId)){
            queryWrapper.eq("teacher_id",teacherId);
        }
        if (!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.eq("subject_parent_id",subjectParentId);
        }
        if (!StringUtils.isEmpty(subjectId)){
            queryWrapper.eq("subject_id",subjectId);
        }
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        eduCourseService.page(coursePage,queryWrapper);
        return ResponseData.responseCall().data("total",coursePage.getTotal()).data("courses",coursePage.getRecords());
    }

    @DeleteMapping("deleteCourse/{courseId}")
    @ApiOperation("根据课程id删除课程")
    public ResponseData deleteCourse(@ApiParam(name="CourseId",value = "课程ID",required = true) @PathVariable String courseId){
        eduCourseService.removeCourseByCourseId(courseId);
        return ResponseData.responseCall();
    }

    @GetMapping("queryPopularCourse")
    @ApiOperation("查询最受欢迎的八个课程")
    public ResponseData queryPopularCourse(){
        List<EduCourse> courseList=eduCourseService.queryPopularCourse();
        return ResponseData.responseCall().data("courseList",courseList);
    }

    @GetMapping("updateCourseBuyCount/{courseId}")
    @ApiOperation("课程购买成功后订单服务调用更新课程购买数量")
    public ResponseData updateCourseBuyCount(@PathVariable String courseId){
        return eduCourseService.updateCourseBuyCount(courseId)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }


}

