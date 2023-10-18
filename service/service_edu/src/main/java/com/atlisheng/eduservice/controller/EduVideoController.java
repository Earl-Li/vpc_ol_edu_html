package com.atlisheng.eduservice.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.VodClient;
import com.atlisheng.eduservice.entity.EduVideo;
import com.atlisheng.eduservice.service.EduVideoService;
import com.atlisheng.servicebase.exceptions.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
@RestController
@RequestMapping("/eduservice/video")
@Api(description = "课程小节管理")

public class EduVideoController {
    @Autowired
    EduVideoService eduVideoService;

    @Autowired
    VodClient vodClient;

    @GetMapping("queryVideo/{videoId}")
    @ApiOperation("根据课时ID查询课时")
    public ResponseData queryVideo(@ApiParam(name="videoId",value = "课时ID",required = true) @PathVariable String videoId){
        EduVideo eduVideo = eduVideoService.getById(videoId);
        return ResponseData.responseCall().data("video",eduVideo);
    }

    @PostMapping("addVideo")
    @ApiOperation("添加课程小节")
    public ResponseData addVideo(@ApiParam(name="video",value = "课程小节",required = true) @RequestBody EduVideo eduVideo){
        return eduVideoService.save(eduVideo)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    @PutMapping("updateVideo")
    @ApiOperation("更新课程小节")
    public ResponseData updateVideo(@ApiParam(name="video",value = "课程小节",required = true)@RequestBody EduVideo eduVideo){
        return eduVideoService.updateById(eduVideo)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }

    /**
     * @param id
     * @return {@link ResponseData }
     * @描述 删除课程小节，如果存在视频则远程调用vod服务删除对应的视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/29
     * @since 1.0.0
     */
    @DeleteMapping("deleteVideo/{id}")
    @ApiOperation("删除课程小节")
    public ResponseData deleteVideo(@ApiParam(name="videoId",value = "课程小节ID",required = true)@PathVariable String id){
        //根据小节id查询出小节视频的ID，判断非空串远程调用vod的删除视频方法并传参视频ID
        String videoSourceId = eduVideoService.getById(id).getVideoSourceId();
        if (!StringUtils.isEmpty(videoSourceId)) {
            ResponseData responseData = vodClient.removeVodVideo(videoSourceId);
            if (!(responseData.getCode()==20000)) {
                throw new CustomException(20001,"删除视频服务器挂掉了");
            }
        }
        return eduVideoService.removeById(id)?ResponseData.responseCall():ResponseData.responseErrorCall();
    }


}

