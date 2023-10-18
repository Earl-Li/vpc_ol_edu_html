package com.atlisheng.eduservice.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.entity.EduChapter;
import com.atlisheng.eduservice.entity.bo.chapter.Chapter;
import com.atlisheng.eduservice.service.EduChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(description = "课程章节管理")
@RequestMapping("/eduservice/chapter")
//@CrossOrigin//原来用nginx使用的@CrossOrigin解决跨域问题已经全部被全局替换删除了
public class EduChapterController {

    @Autowired
    private EduChapterService eduChapterService;

    @GetMapping("chapterList/{courseId}")
    @ApiOperation("嵌套封装章节数据")
    public ResponseData getChaptersByCourseId(@ApiParam(name = "courseId",value="课程id",required = true) @PathVariable("courseId") String courseId){
        List<Chapter> chapters=eduChapterService.getChaptersByCourseId(courseId);
        System.out.println(courseId);
        return ResponseData.responseCall().data("chapters",chapters);
    }

    @PostMapping("addChapter")
    @ApiOperation("新增章节")
    public ResponseData addChapter(@ApiParam(name="chapter",value="课程章节",required = true) @RequestBody EduChapter eduChapter){
        return eduChapterService.save(eduChapter)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    @GetMapping("queryChapter/{chapterId}")
    @ApiOperation("查询章节")
    public ResponseData queryChapter(@ApiParam(name="chapterId",value="课程章节ID",required = true) @PathVariable String chapterId){
        EduChapter chapter = eduChapterService.getById(chapterId);
        return ResponseData.responseCall().data("chapter",chapter);
    }

    @PutMapping("updateChapter")
    @ApiOperation("修改章节")
    public ResponseData updateChapter(@ApiParam(name="chapter",value="课程章节",required = true) @RequestBody EduChapter eduChapter){
        return eduChapterService.updateById(eduChapter)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    @DeleteMapping("deleteChapter/{chapterId}")
    @ApiOperation("删除章节")
    public ResponseData deleteChapter(@ApiParam(name = "chapterId",value = "课程ID",required = true) @PathVariable String chapterId){
        return eduChapterService.deleteChapter(chapterId)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }
}

