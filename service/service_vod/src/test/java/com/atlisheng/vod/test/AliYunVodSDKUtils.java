package com.atlisheng.vod.test;

import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;

public class AliYunVodSDKUtils {
    /**
     * @param accessKeyId
     * @param accessKeySecret
     * @return {@link DefaultAcsClient }
     * @描述 视频点播操作对象的初始化操作，获取DefaultAcsClient对象
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/26
     * @since 1.0.0
     */
    public static DefaultAcsClient initVodClient(String accessKeyId, String
            accessKeySecret) throws ClientException {
        String regionId = "cn-shanghai"; // 点播服务接入区域
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId,
                accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }
}
