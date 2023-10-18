package com.atlisheng.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.atlisheng.vod.service.VodService;
import com.atlisheng.vod.utils.ALiYunVodUtil;
import com.atlisheng.vod.utils.ConstantProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {
    /**
     * @param file
     * @return {@link String }
     * @描述 以文件的方式上传视频到阿里云视频点播
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/27
     * @since 1.0.0
     */
    @Override
    public String uploadVideoByFile(MultipartFile file) {
        try {
            String fileName=file.getOriginalFilename();
            String title=fileName.substring(0,fileName.lastIndexOf('.'))+" upload by fileAndInputStream";
            InputStream inputStream=file.getInputStream();
            UploadStreamRequest request = new UploadStreamRequest(ConstantProperties.ACCESS_KEY_ID, ConstantProperties.ACCESS_KEY_SECRET, title, fileName, inputStream);
            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            String videoId;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
            }
            return videoId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param videoId
     * @描述 根据视频ID删除阿里云VOD上的视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/28
     * @since 1.0.0
     */
    @Override
    public void removeVodVideo(String videoId) {
        try {
            DefaultAcsClient client = ALiYunVodUtil.initVodClient(
                    ConstantProperties.ACCESS_KEY_ID,
                    ConstantProperties.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoId);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        } catch (ClientException e) {
            e.printStackTrace();
            throw new CustomException(20001,"视频删除失败");
        }
    }

    /**
     * @param videoIdList
     * @描述 根据多个视频id批量删除视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/30
     * @since 1.0.0
     */
    @Override
    public void removeVodVideoByIds(List<String> videoIdList) {
        try {
            DefaultAcsClient client = ALiYunVodUtil.initVodClient(
                    ConstantProperties.ACCESS_KEY_ID,
                    ConstantProperties.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            String videoIdsString = String.join(",", videoIdList);
            request.setVideoIds(videoIdsString);
            DeleteVideoResponse response = client.getAcsResponse(request);
            System.out.print("RequestId = " + response.getRequestId() + "\n");
        } catch (ClientException e) {
            e.printStackTrace();
            throw new CustomException(20001,"视频删除失败");
        }
    }

    /**
     * @param videoId
     * @return {@link String }
     * @描述 根据视频id获取视频播放凭证
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     */
    @Override
    public String getPlayAuth(String videoId) {
        //初始化客户端、请求对象和相应对象
        DefaultAcsClient client = ALiYunVodUtil.initVodClient(ConstantProperties.ACCESS_KEY_ID, ConstantProperties.ACCESS_KEY_SECRET);
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
        try {
            //设置请求参数
            request.setVideoId(videoId);
            //获取请求响应
            response = client.getAcsResponse(request);
            //返回播放凭证
            return response.getPlayAuth();
        } catch (Exception e) {
            throw new CustomException(20001,e.getLocalizedMessage());
        }
    }
}
