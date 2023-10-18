package com.atlisheng.utils.excelutiltest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<DemoExcelData> {
    @Override
    public void invoke(DemoExcelData demoExcelData, AnalysisContext analysisContext) {
        System.out.println("表格内容:"+demoExcelData);//表格每行被自动封装到对象demoExcelData中
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头内容:"+headMap);//表头内容一行记录会以index,表头内容的形式封装到headMap中
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
