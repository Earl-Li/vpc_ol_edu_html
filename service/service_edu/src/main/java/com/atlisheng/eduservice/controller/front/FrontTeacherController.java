package com.atlisheng.eduservice.controller.front;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.EduTeacher;
import com.atlisheng.eduservice.service.EduCourseService;
import com.atlisheng.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Api(description = "前台讲师管理")
@RequestMapping("/eduservice/front")

public class FrontTeacherController {

    @Autowired
    EduTeacherService eduTeacherService;

    @Autowired
    EduCourseService eduCourseService;

    /**
     * @param curPage
     * @param limit
     * @return {@link ResponseData }
     * @描述 分页查询所有讲师
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/09
     * @since 1.0.0
     */
    @ApiOperation("前台讲师分页查询")
    @GetMapping( "pageTeacherList/{curPage}/{limit}")
    public ResponseData pageFrontTeacherList(
            @ApiParam(name = "curPage", value = "当前页码", required = true)
            @PathVariable long curPage,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable long limit){
        Page<EduTeacher> pageParam = new Page<EduTeacher>(curPage,limit);
        Map<String, Object> map = eduTeacherService.pageFrontTeacherList(pageParam);
        //page的数据只有五条，远远没有后端获取数据封装map返回给前端的多
        //return ResponseData.responseCall().data("pageData",pageParam);
        return ResponseData.responseCall().data(map);
    }

    /**
     * @param teacherId
     * @return {@link ResponseData }
     * @描述 根据讲师id查询讲师详情和讲师教授课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/09
     * @since 1.0.0
     */
    @GetMapping("getTeacherInfoById/{teacherId}")
    @ApiOperation("根据讲师id查询讲师详情和课程列表")
    public ResponseData getTeacherInfoById(
            @ApiParam(name = "teacherId", value = "讲师ID", required = true)
            @PathVariable String teacherId){
        EduTeacher eduTeacher = eduTeacherService.getById(teacherId);
        List<EduCourse> courseList=eduCourseService.getCourseInfoByTeacherId(teacherId);
        return ResponseData.responseCall().data("teacher",eduTeacher).data("courses",courseList);
    }



}
