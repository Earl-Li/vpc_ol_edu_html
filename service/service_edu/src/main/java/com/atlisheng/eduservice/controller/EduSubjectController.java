package com.atlisheng.eduservice.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.entity.bo.subject.FirstLevelSubject;
import com.atlisheng.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-09-10
 */
@RestController
@RequestMapping("/eduservice/subject")

@Api(description = "课程分类管理")
public class EduSubjectController {

    @Autowired
    private EduSubjectService eduSubjectService;//controller注入eduSubjectService，这个对象一直传入EduSubjectServiceImpl，疑问spring的IoC组件能否通过this直接传入

    /**
     * @param file
     * @return {@link ResponseData }
     * @描述 添加课程分类，获取上传过来的表格文件，把文件内容读取出来
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/11
     * @since 1.0.0
     */
    @PostMapping("addSubject")
    @ApiOperation("读取上传课程分类文件")
    public ResponseData addSubject(MultipartFile file){
        //调用eduSubjectServiceImpl中的saveSubject方法来读取表格内容并存入数据库
        eduSubjectService.saveSubject(file,eduSubjectService);
        return ResponseData.responseCall();
    }

    /**
     * @return {@link ResponseData }
     * @描述 查询数据库所有课程并按一级目录二级目录整理成list集合返回给前端
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/11
     * @since 1.0.0
     */
    @GetMapping("getAllSubject")
    @ApiOperation("获取所有课程信息")
    public ResponseData getAllSubject(){
        List<FirstLevelSubject> subjects=eduSubjectService.getAllSubject();
        return ResponseData.responseCall().data("subjects",subjects);
    }


}

