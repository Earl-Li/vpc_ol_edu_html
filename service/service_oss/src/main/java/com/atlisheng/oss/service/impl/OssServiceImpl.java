package com.atlisheng.oss.service.impl;

import com.atlisheng.oss.service.OssService;
import com.atlisheng.oss.utils.ConstantProperties;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    /**
     * @param file
     * @return {@link String }
     * @描述 上传讲师头像文件到阿里云oss，由于SpringBoot将头像封装成文件，这里需要使用文件上传流的java代码，直接去oss官网复制
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/04
     * @since 1.0.0
     */
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        // 填写阿里云四大oss信息
        String endpoint = ConstantProperties.END_POINT;
        String bucketName = ConstantProperties.BUCKET_NAME;
        String accessKeyId = ConstantProperties.KEY_ID;
        String accessKeySecret=ConstantProperties.ACCESS_KEY_SECRET;


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);

        try {
            //上传文件流
            InputStream inputStream = file.getInputStream();
            //获取上传文件名称
            String originalFilename = file.getOriginalFilename();
            originalFilename=new DateTime().toString("yyyy/MM/dd")+"/"+UUID.randomUUID().toString().replace("-","")+originalFilename;
            //调用oss方法实现上传（需要拼参数bucket名称、上传到oss的文件路径和名称、上传文件输入流）
            ossClient.putObject(bucketName, originalFilename, inputStream);
            //上传后需要把上传到阿里云oss的路径手动拼接出来存放在数据库,访问地址的规则是"https://"+bucketName+"."+endpoint+"/"+originalFilename
            String avatarUrl="https://"+bucketName+"."+endpoint+"/"+originalFilename;
            return avatarUrl;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            return null;
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
