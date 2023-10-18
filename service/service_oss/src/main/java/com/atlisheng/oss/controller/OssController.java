package com.atlisheng.oss.controller;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/eduoss/fileoss")

@Api(description = "阿里云对象存储OSS图片管理")
public class OssController {

    @Autowired
    private OssService ossService;

    /**
     * @return {@link ResponseData }
     * @描述  上传头像的方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/04
     * @since 1.0.0
     */
    @PostMapping
    @ApiOperation("讲师头像上传")
    public ResponseData uploadOssFile(@ApiParam(name="file",value = "图片文件",required = true) MultipartFile file){
        //通过请求获取上传文件会自动封装到MultipartFile中
        //通过自定义OssService的uploadFileAvatar返回上传文件的访问地址
        String FileUrl=ossService.uploadFileAvatar(file);
        return ResponseData.responseCall().data("url",FileUrl);
    }


}
