package com.atlisheng.eduservice.controller.front;

import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.UserClient;
import com.atlisheng.eduservice.entity.EduComment;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.service.EduCommentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description = "前台评论管理")
@RestController

@RequestMapping("/eduservice/frontcomment")
public class FrontCommentController {
    @Autowired
    private EduCommentService commentService;
    @Autowired
    private UserClient userClient;

    /**
     * @param curPage
     * @param limit
     * @param courseId
     * @return {@link ResponseData }
     * @描述 根据课程id分页查询评论，没有实现评论嵌套，即回复评论的评论
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/11
     * @since 1.0.0
     *///根据课程id查询评论列表
    @ApiOperation(value = "评论分页列表")
    @GetMapping("getCommentByCourseId/{courseId}/{curPage}/{limit}")
    public ResponseData getCommentByCourseId(
            @ApiParam(name = "curPage", value = "当前页码", required = true)
            @PathVariable("curPage") Long curPage,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable("limit") Long limit,
            @ApiParam(name = "courseId", value = "查询对象", required = true)
            @PathVariable("courseId") String courseId) {
        Page<EduComment> pageParam = new Page<>(curPage, limit);
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        commentService.page(pageParam,wrapper);
        List<EduComment> commentList = pageParam.getRecords();
        Map<String, Object> map = new HashMap<>();
        map.put("comments", commentList);
        map.put("curPage", pageParam.getCurrent());
        map.put("pages", pageParam.getPages());
        map.put("size", pageParam.getSize());
        map.put("total", pageParam.getTotal());
        map.put("hasNext", pageParam.hasNext());
        map.put("hasPrevious", pageParam.hasPrevious());
        return ResponseData.responseCall().data(map);
    }

    /**
     * @param comment
     * @param request
     * @return {@link ResponseData }
     * @描述 添加评论，前端需要传入评论内容，课程id，
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/11
     * @since 1.0.0
     */
    @ApiOperation(value = "添加评论")
    @PostMapping("addComment")
    public ResponseData addComment(@RequestBody EduComment comment, HttpServletRequest request) {
        String userId = JwtUtils.getMemberIdByJwtToken(request);
        if(StringUtils.isEmpty(userId)) {
            return ResponseData.responseCall().code(28004).message("请先登录");
        }
        comment.setUserId(userId);
        ResponseData response = userClient.getLoginInfo(userId);
        Map<String, Object> userInfo =  response.getData();
        comment.setNickname((String)userInfo.get("nickname"));
        comment.setAvatar((String)userInfo.get("avatar"));
        commentService.save(comment);
        return ResponseData.responseCall();
    }

}
