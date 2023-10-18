package com.atlisheng.vod.test;

import com.aliyun.oss.ClientException;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoRequest;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import org.junit.Test;

import java.util.List;

public class AliYunVodSDKTest {
    private String accessKeyId="LTAI5t8W1kDjtkhA399nYGAh";
    private String accessKeySecret="yAKCPaOaUtr5ruK4tJpOpfbhld69oy";

    /**
     * 本地文件上传接口
     */
    @Test
    public void testUploadVideo() {
        String title="6 - What If I Want to Move Faster-LocalUpload by sdk";//文件上传后阿里云上对应的名字
        String fileName="E:\\JavaStudy\\project\\ol_edu\\resources\\6 - What If I Want to Move Faster.mp4";
        UploadVideoRequest request = new UploadVideoRequest(accessKeyId, accessKeySecret, title, fileName);

        /* 可指定分片上传时每个分片的大小，默认为2M字节,将视频分片存储，每个片2M大小，最终组成一个完整视频 */
        request.setPartSize(2 * 1024 * 1024L);
        /* 可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定）*/
        request.setTaskNum(1);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);

        System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
        if (response.isSuccess()) {//这个是判断回调函数是否有返回值
            System.out.print("VideoId=" + response.getVideoId() + "\n");//获取视频的id
        } else {
            /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
            System.out.print("VideoId=" + response.getVideoId() + "\n");
            System.out.print("ErrorCode=" + response.getCode() + "\n");
            System.out.print("ErrorMessage=" + response.getMessage() + "\n");
        }
    }

    /**
     * @描述 通过视频id获取视频播放凭证
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/26
     * @since 1.0.0
     */
    @Test
    public void testGetVideoPlayAuth() throws ClientException {
        //初始化客户端、请求对象和相应对象
        DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(accessKeyId,
                accessKeySecret);
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
        try {
            //设置请求参数
            request.setVideoId("98d5be205c3971eebfbd0675a0ec0102");
            //获取请求响应
            response = client.getAcsResponse(request);
            //输出请求结果
            //播放凭证
            System.out.print("PlayAuth = " + response.getPlayAuth() + "\n");
            //VideoMeta信息
            System.out.print("VideoMeta.Title = " + response.getVideoMeta().getTitle() + "\n");
        } catch (Exception e) {
            System.out.print("ErrorMessage = " + e.getLocalizedMessage());
        }
        System.out.print("RequestId = " + response.getRequestId() + "\n");
    }

    /**
     * @描述 通过视频id获取视频播放地址
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/26
     * @since 1.0.0
     */
    @Test
    public void testGetVideoPlayInfoList() throws ClientException {
        //初始化客户端、请求对象和相应对象
        DefaultAcsClient client = AliYunVodSDKUtils.initVodClient(accessKeyId,
                accessKeySecret);
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        GetPlayInfoResponse response = new GetPlayInfoResponse();
        try {
            //设置请求参数
            //request.setVideoId("98d5be205c3971eebfbd0675a0ec0102");
            request.setVideoId("13d6a4705c5a71ee929e0674a2ce0102");
            //获取请求响应
            response = client.getAcsResponse(request);
            //输出请求结果
            List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
            for (GetPlayInfoResponse.PlayInfo playInfo: playInfoList) {
                //获取视频的地址
                System.out.println("PlayInfo.PlayURL =" + playInfo.getPlayURL()+"\n");
            }
            //获取视频的名称
            System.out.println("VideoBase.Title =" + response.getVideoBase().getTitle()+"\n");
        } catch (Exception e) {
            System.out.print("ErrorMessage = " + e.getLocalizedMessage());
        }
        System.out.print("RequestId = " + response.getRequestId() + "\n");
    }
}
