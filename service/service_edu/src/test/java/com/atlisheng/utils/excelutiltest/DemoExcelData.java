package com.atlisheng.utils.excelutiltest;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemoExcelData {
    //设置实体类与表格表头信息的对应关系，没有表创建表格时会自动写成表头
    @ExcelProperty(value = "学生编号",index = 0)
    private Integer studentNo;
    @ExcelProperty(value = "学生姓名",index=1)
    private String studentName;
}
