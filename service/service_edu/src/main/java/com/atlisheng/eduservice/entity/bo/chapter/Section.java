package com.atlisheng.eduservice.entity.bo.chapter;

import lombok.Data;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 课程小节
 * @创建日期 2023/09/20
 * @since 1.0.0
 */
@Data
public class Section {
    private String id;
    private String title;
    private String videoSourceId;
    private String isFree;
}
