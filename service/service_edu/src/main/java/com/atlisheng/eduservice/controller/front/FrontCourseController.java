package com.atlisheng.eduservice.controller.front;

import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.OrderClient;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.bo.chapter.Chapter;
import com.atlisheng.eduservice.entity.bo.course.FrontCourseInfo;
import com.atlisheng.commonutils.entity.vo.FrontCourseQueryFactor;
import com.atlisheng.eduservice.service.EduChapterService;
import com.atlisheng.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(description = "前台课程管理")
@RestController

@RequestMapping("/eduservice/front")
public class FrontCourseController {
    @Autowired
    EduCourseService eduCourseService;
    @Autowired
    EduChapterService eduChapterService;
    @Autowired
    OrderClient orderClient;

    @ApiOperation("多条件分页查询课程列表")
    @PostMapping("pageFactorCourse/{curPage}/{limit}")
    public ResponseData pageFactorCourse(
            @ApiParam(name = "curPage", value = "当前页码", required = true)
            @PathVariable Long curPage,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
            @RequestBody(required = false) FrontCourseQueryFactor courseQueryFactor){
        Page<EduCourse> pageParam = new Page<EduCourse>(curPage, limit);
        Map<String, Object> map = eduCourseService.pageFactorCourse(pageParam, courseQueryFactor);
        return ResponseData.responseCall().data(map);
    }

    @ApiOperation("根据课程id查询前台课程信息详情")
    @GetMapping("getFrontCourseInfo/{courseId}")
    public ResponseData getFrontCourseInfo(
            @ApiParam(name = "courseId", value = "课程ID", required = true)
            @PathVariable String courseId,
            @ApiParam(name = "request", value = "请求对象", required = true)
            HttpServletRequest request){
        //查询课程信息和讲师信息
        FrontCourseInfo course = eduCourseService.getFrontCourseInfoByCourseId(courseId);
        //查询当前课程的章节信息
        List<Chapter> chapters = eduChapterService.getChaptersByCourseId(courseId);
        //查询课程购买状态
        String userId = JwtUtils.getMemberIdByJwtToken(request);
        boolean isBuy=false;
        if(!StringUtils.isEmpty(userId)){
            isBuy = orderClient.confirmCourseBuyStatus(JwtUtils.getMemberIdByJwtToken(request),courseId);
        }
        return ResponseData.responseCall().data("course", course).data("chapters", chapters).data("isBuy",isBuy);
    }
}
