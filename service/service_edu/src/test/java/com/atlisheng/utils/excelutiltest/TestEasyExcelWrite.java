package com.atlisheng.utils.excelutiltest;



import com.alibaba.excel.EasyExcel;
import com.atlisheng.eduservice.service.EduSubjectService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 实现使用EasyExcel进行写操作
 * @创建日期 2023/09/10
 * @since 1.0.0
 */
public class TestEasyExcelWrite {

    public static void main(String[] args) {
        //定义写入文件夹的地址，注意没有对应文件会自动创建
        //String fileName="E:\\JavaStudy\\project\\ol_edu\\student.xlsx";
        //调用EasyExcel的write方法实现写操作,这种方式会自动关流
        //EasyExcel.write(fileName,DemoExcelData.class).sheet("学生列表").doWrite(getData());

        //实现excel的读操作
        String fileName="E:\\JavaStudy\\project\\ol_edu\\student.xlsx";
        EasyExcel.read(fileName,DemoExcelData.class,new ExcelListener()).sheet().doRead();
    }


    private static List<DemoExcelData> getData() {
        List<DemoExcelData> excelData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            excelData.add(new DemoExcelData(i, "lucy" + i));
        }
        return excelData;
    }

}
