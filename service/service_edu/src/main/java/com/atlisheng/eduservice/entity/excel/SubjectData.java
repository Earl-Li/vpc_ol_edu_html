package com.atlisheng.eduservice.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 subject.xlsx对应的表格数据记录
 * @创建日期 2023/09/10
 * @since 1.0.0
 */
@Data
public class SubjectData {

    @ExcelProperty(index=0)
    private String firstSubjectName;

    @ExcelProperty(index=1)
    private String secondSubjectName;
}
