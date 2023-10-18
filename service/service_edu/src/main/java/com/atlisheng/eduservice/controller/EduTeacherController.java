package com.atlisheng.eduservice.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.entity.EduTeacher;
import com.atlisheng.commonutils.entity.vo.TeacherQueryFactor;
import com.atlisheng.eduservice.service.EduTeacherService;
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
 * 讲师 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-08-27
 */
@RestController
@RequestMapping("/eduservice/teacher")
@Api(description = "课程讲师管理")

public class EduTeacherController {
    //自动注入service实现类，该实现类中的baseMapper就是mp的动态代理类，teacherService的持久化操作就是baseMapper的持久化操作
    @Autowired
    private EduTeacherService teacherService;

    /**
     * @return {@link List }<{@link EduTeacher }>
     * @描述 查询数据库所有讲师数据，list方法就是baseMapper的selectList方法
     * 访问地址：http://localhost:8001/eduservice/teacher/finaAll
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @GetMapping("findAll")
    @ApiOperation(value = "查询所有讲师列表")
    public ResponseData findAllTeacher(){
        List<EduTeacher> teacherList = teacherService.list(null);
        return ResponseData.responseCall().data("items",teacherList);
    }

    /**
     * @param id id
     * @return boolean
     * @描述 根据id逻辑删除讲师，对应baseMapper中的deleteById方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @DeleteMapping("{id}")
    @ApiOperation(value = "通过讲师ID逻辑删除讲师")
    public ResponseData logicRemoveTeacher(@ApiParam(name = "id",value = "讲师ID",required = true) @PathVariable String id){
        boolean status = teacherService.removeById(id);//只要数据库有响应就会返回true，即便响应0
        return status?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    /**
     * @param current 当前
     * @param limit   限制
     * @return {@link ResponseData }
     * @描述 分页查询讲师记录
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @GetMapping("pageTeacher/{current}/{limit}")
    @ApiOperation(value = "分页查询全部讲师")
    public ResponseData findAllTeacherPaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                             @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit){
        Page<EduTeacher> teacherPage = new Page<>(current, limit);
        /*try{
            int i=10/0;
        }catch (Exception e){
            throw new CustomException(20001,"执行了自定义异常处理...");
        }*/
        //int i=10/0;//这种会由系统抛出算术异常
        teacherService.page(teacherPage,null);
        return ResponseData.responseCall().data("total",teacherPage.getTotal()).data("rows",teacherPage.getRecords());//getRecords是获取当前页的所有记录集合
    }

    /**
     * @param current            当前页
     * @param limit              每页记录数
     * @param teacherQueryFactor 讲师查询条件
     * @return {@link ResponseData }
     * @描述 讲师多条件组合带分页查询
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/27
     * @since 1.0.0
     */
    @PostMapping("pageFactorTeacher/{current}/{limit}")
    @ApiOperation(value = "讲师多条件组合带分页查询")
    public ResponseData findFactorTeacherPaging(@ApiParam(name = "current",value = "当前页",required = true) @PathVariable Integer current,
                                                @ApiParam(name = "limit",value = "每页记录条数",required = true) @PathVariable Integer limit,
                                                @ApiParam(name = "teacherQueryFactor",value = "讲师筛选条件") @RequestBody(required = false) TeacherQueryFactor teacherQueryFactor){//@RequestBody将json数据封装到对应的对象中
        Page<EduTeacher> teacherPage = new Page<>(current,limit);
        QueryWrapper<EduTeacher> queryWrapper = new QueryWrapper<>();
        String teacherName = teacherQueryFactor.getName();
        Integer teacherLevel = teacherQueryFactor.getLevel();
        String beginTime = teacherQueryFactor.getBeginTime();
        String endTime = teacherQueryFactor.getEndTime();
        queryWrapper.orderByDesc("gmt_Create");
        if (!StringUtils.isEmpty(teacherName)){
            queryWrapper.like("name",teacherName);
        }
        if (!StringUtils.isEmpty(teacherLevel)){
            queryWrapper.eq("level",teacherLevel);
        }
        if (!StringUtils.isEmpty(beginTime)){
            queryWrapper.ge("gmt_create",beginTime);
        }
        if (!StringUtils.isEmpty(endTime)){
            queryWrapper.le("gmt_create",endTime);
        }
        teacherService.page(teacherPage,queryWrapper);
        return ResponseData.responseCall().data("total",teacherPage.getTotal()).data("rows",teacherPage.getRecords());
    }

    /**
     * @param eduTeacher edu老师
     * @return {@link ResponseData }
     * @描述 添加讲师到列表
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/28
     * @since 1.0.0
     */
    @PostMapping("addTeacher")
    @ApiOperation("添加讲师到列表")
    public ResponseData addTeacher(@ApiParam(name = "eduTeacher",value = "讲师信息",required = true) @RequestBody EduTeacher eduTeacher){
        boolean status = teacherService.save(eduTeacher);
        return status?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    /**
     * @param id id
     * @return {@link ResponseData }
     * @描述 根据讲师ID查询讲师信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/28
     * @since 1.0.0
     */
    @GetMapping("queryTeacher/{id}")
    @ApiOperation("根据ID查询讲师信息")
    public ResponseData queryTeacherByID(@ApiParam(name = "id",value = "讲师ID",required = true) @PathVariable String id){
        EduTeacher eduTeacher = teacherService.getById(id);
        return ResponseData.responseCall().data("items",eduTeacher);
    }

    /**
     * @param eduTeacher edu老师
     * @return {@link ResponseData }
     * @描述 根据讲师ID更新讲师信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/28
     * @since 1.0.0
     */
    @PutMapping("updateTeacher/{id}")
    @ApiOperation("根据ID更新讲师信息")
    public ResponseData updateTeacher(@ApiParam(name = "id",value = "讲师id",required = true) @PathVariable String id,
                                      @ApiParam(name = "eduTeacher",value = "讲师更新信息") @RequestBody EduTeacher eduTeacher){
        boolean status = teacherService.updateById(eduTeacher.setId(id));
        return status?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    /**
     * @return {@link ResponseData }
     * @描述 查询最有资质的前4条名师
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @GetMapping("queryPopularTeacher")
    @ApiOperation("查询前4条名师")
    public ResponseData queryPopularTeacher(){
        List<EduTeacher> teacherList=teacherService.queryPopularTeacher();
        return ResponseData.responseCall().data("teacherList",teacherList);
    }

    /**
     * @param teacherId
     * @return {@link ResponseData }
     * @描述 根据讲师id查询讲师姓名
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("queryTeacherName/{teacherId}")
    @ApiOperation("根据ID查询讲师姓名")
    public ResponseData queryTeacherNameById(@ApiParam(name = "teacherId",value = "讲师ID",required = true) @PathVariable String teacherId){
        EduTeacher eduTeacher = teacherService.getById(teacherId);
        return ResponseData.responseCall().data("teacherName",eduTeacher.getName());
    }



}

