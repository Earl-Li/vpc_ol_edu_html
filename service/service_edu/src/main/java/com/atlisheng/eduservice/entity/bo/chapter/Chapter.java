package com.atlisheng.eduservice.entity.bo.chapter;

import lombok.Data;

import java.util.List;


/**
 * @author Earl
 * @version 1.0.0
 * @描述  章节分类
 * @创建日期 2023/09/20
 * @since 1.0.0
 */
@Data
public class Chapter {
    private String id;
    private String title;

    private List<Section> children;
}
