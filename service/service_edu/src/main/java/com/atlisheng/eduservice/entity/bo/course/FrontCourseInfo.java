package com.atlisheng.eduservice.entity.bo.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = "前台课程详情对象", description = "前台课程信息查询结果封装")
@Data
public class FrontCourseInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    @ApiModelProperty(value = "课程标题")
    private String title;
    @ApiModelProperty(value = "课程销售价格，设置为0则可免费观看")
    private BigDecimal price;
    @ApiModelProperty(value = "总课时")
    private Integer courseTotalTime;
    @ApiModelProperty(value = "课程封面图片路径")
    private String cover;
    @ApiModelProperty(value = "销售数量")
    private Long buyCount;
    @ApiModelProperty(value = "浏览数量")
    private Long viewCount;
    @ApiModelProperty(value = "课程简介")
    private String description;
    @ApiModelProperty(value = "讲师ID")
    private String teacherId;
    @ApiModelProperty(value = "讲师姓名")
    private String teacherName;
    @ApiModelProperty(value = "讲师资历,一句话说明讲师")
    private String intro;
    @ApiModelProperty(value = "讲师头像")
    private String avatar;
    @ApiModelProperty(value = "课程一级分类ID")
    private String firstLevelSubjectId;
    @ApiModelProperty(value = "课程一级分类名称")
    private String firstLevelSubjectTitle;
    @ApiModelProperty(value = "课程类别ID")
    private String secondLevelSubjectId;
    @ApiModelProperty(value = "类别名称")
    private String secondLevelSubjectTitle;
}
