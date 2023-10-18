package com.atlisheng.eduservice.client.impl;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduservice.client.VodClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodClientDegrade implements VodClient {
    @Override
    public ResponseData removeVodVideo(String videoId) {
        return ResponseData.responseErrorCall().message("删除单个视频服务time out...");
    }

    @Override
    public ResponseData removeVodVideoByIds(List<String> videoIdList) {
        return ResponseData.responseErrorCall().message("删除多个视频服务time out...");
    }
}
