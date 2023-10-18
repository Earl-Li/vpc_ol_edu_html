package com.atlisheng.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    /**
     * @param file
     * @return {@link String }
     * @描述 将头像上传到阿里云oss
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/04
     * @since 1.0.0
     */
    String uploadFileAvatar(MultipartFile file);
}
