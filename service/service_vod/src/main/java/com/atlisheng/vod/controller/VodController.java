package com.atlisheng.vod.controller;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.vod.service.VodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController

@RequestMapping("eduvod/filevod")
@Api(description = "阿里云视频点播视频管理")
public class VodController {
    @Autowired
    VodService vodService;

    /**
     * @param file
     * @return {@link ResponseData }
     * @描述 添加视频到阿里云视频点播
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @PostMapping("uploadVideo")
    @ApiOperation("视频文件上传至阿里云VOD")
    public ResponseData uploadVideo(@ApiParam(name="file",value = "视频文件",required = true) MultipartFile file){
        String videoId = vodService.uploadVideoByFile(file);
        return ResponseData.responseCall().data("videoId",videoId);
    }

    /**
     * @param videoId
     * @return {@link ResponseData }
     * @描述 根据单个视频id删除阿里云上多个视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @DeleteMapping("removeVodVideo/{videoId}")
    @ApiOperation("根据视频id删除阿里云VOD上的视频")
    public ResponseData removeVodVideo(@ApiParam(name="videoId",value = "视频ID",required = true) @PathVariable String videoId){
        vodService.removeVodVideo(videoId);
        return ResponseData.responseCall().message("视频删除成功");
    }

    /**
     * @param videoIdList
     * @return {@link ResponseData }
     * @描述 根据多个视频id批量删除视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @DeleteMapping("removeVodVideoByIds")
    @ApiOperation("根据多个视频id批量删除视频")
    public ResponseData removeVodVideoByIds(@ApiParam(name="videoIds",value = "批量视频ID",required = true)@RequestParam("videoIdList") List<String> videoIdList){
        vodService.removeVodVideoByIds(videoIdList);
        return ResponseData.responseCall().message("视频删除成功");
    }

    /**
     * @param videoId
     * @return {@link ResponseData }
     * @描述 根据视频id获取视频播放凭证
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @ApiOperation("根据视频id获取视频播放凭证")
    @GetMapping("getPlayAuth/{videoId}")
    public ResponseData getPlayAuth(
            @ApiParam(name="videoId",value = "视频id",required = true)
            @PathVariable String videoId ){
        String videoAuth=vodService.getPlayAuth(videoId);
        return ResponseData.responseCall().data("videoAuth",videoAuth);
    }
}
