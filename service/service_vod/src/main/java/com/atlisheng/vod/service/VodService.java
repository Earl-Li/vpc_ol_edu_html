package com.atlisheng.vod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    /**
     * @param file
     * @return {@link String }
     * @描述 以文件的方式上传视频到阿里云视频点播
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/27
     * @since 1.0.0
     */
    String uploadVideoByFile(MultipartFile file);

    /**
     * @param videoId
     * @描述 根据视频ID删除阿里云VOD上的视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/28
     * @since 1.0.0
     */
    void removeVodVideo(String videoId);

    /**
     * @param videoIds
     * @描述 根据多个视频id批量删除视频
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/30
     * @since 1.0.0
     */
    void removeVodVideoByIds(List<String> videoIds);

    /**
     * @return {@link String }
     * @描述 根据视频id获取视频播放凭证
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/10
     * @since 1.0.0
     * @param videoId
     */
    String getPlayAuth(String videoId);
}
