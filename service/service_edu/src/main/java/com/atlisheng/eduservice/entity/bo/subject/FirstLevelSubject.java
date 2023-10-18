package com.atlisheng.eduservice.entity.bo.subject;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 一级课程分类
 * @创建日期 2023/09/11
 * @since 1.0.0
 */
@Data
public class FirstLevelSubject {
    private String id;
    private String title;

    //一级分类中的二级分类
    private List<SecondLevelSubject> children=new ArrayList<>();
}
