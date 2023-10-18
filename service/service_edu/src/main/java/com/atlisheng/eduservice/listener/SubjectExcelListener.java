package com.atlisheng.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atlisheng.eduservice.entity.EduSubject;
import com.atlisheng.eduservice.entity.excel.SubjectData;
import com.atlisheng.eduservice.service.EduSubjectService;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 SubjectExcelListener不能交给Spring进行管理【为什么？】，需要自己手动new，就不能注入其他IoC组件，不能用service、mapper实现数据库操作
 * 解决办法：在service中new SubjectExcelListener的时候就将对应的数据库操作对象用构造方法注入的方式注入进来
 * @创建日期 2023/09/10
 * @since 1.0.0
 */
public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    public EduSubjectService subjectService;

    public SubjectExcelListener() {
    }

    public SubjectExcelListener(EduSubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /**
     * @param subjectData
     * @param analysisContext
     * @描述 读取Excel中的内容，一行一行进行读取，注意如果一级目录存在相同内容需要判断不能重复存入数据库，如果读取的数据为null，
     * 表名数据库中已经没有数据了，这儿有问题，始终都会有读到最后没有数据的情况，或者中间某行数据没有记录的情况，这儿直接抛异常没问题吗？
     * 讲的不清楚，EasyExcel以后自己学了再说，包括返回对象为null的情况对应表格的何种情况
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/10
     * @since 1.0.0
     */
    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if (subjectData==null){
            //这里一定要搞清楚subjectData为null到底是某行数据为null还是表格压根就没有数据
            throw new CustomException(20001,"文件没有数据");
        }
        //一级数据重复的现象很普遍，需要判断一级数据不能重复添加
        EduSubject firstSubject = exitFirstSubject(subjectData.getFirstSubjectName());
        if (firstSubject==null){//如果一级分类不存在则存入数据库
            firstSubject = new EduSubject();
            firstSubject.setParentId("0");
            firstSubject.setTitle(subjectData.getFirstSubjectName());
            subjectService.save(firstSubject);
        }
        String pid = firstSubject.getId();//不管一级分类有没有id值都会保存一级分类的id值
        if (exitSecondSubject(subjectData.getSecondSubjectName(),pid)==null){
            EduSubject secondEduSubject = new EduSubject();
            secondEduSubject.setParentId(pid);
            secondEduSubject.setTitle(subjectData.getSecondSubjectName());
            subjectService.save(secondEduSubject);
        }

    }

    /**
     * @param
     * @param name
     * @return {@link EduSubject }
     * @描述 判断一级分类不能重复添加，这里每行都要调用很浪费资源，不如读一次以后直接把title字段加入缓存
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/10
     * @since 1.0.0
     */
    private EduSubject exitFirstSubject(String name){
        QueryWrapper<EduSubject> eduSubjectQueryWrapper = new QueryWrapper<>();
        eduSubjectQueryWrapper.eq("title",name).eq("parent_id","0");
        EduSubject subject = subjectService.getOne(eduSubjectQueryWrapper);
        return subject;
    }

    /**
     * @param name
     * @param pid
     * @return {@link EduSubject }
     * @描述  判断二级分类不能重复添加，这里每行都要调用很浪费资源，不如读一次以后直接把title字段加入缓存
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/10
     * @since 1.0.0
     */
    private EduSubject exitSecondSubject(String name,String pid){
        QueryWrapper<EduSubject> eduSubjectQueryWrapper = new QueryWrapper<>();
        eduSubjectQueryWrapper.eq("title",name).eq("parent_id",pid);
        EduSubject subject = subjectService.getOne(eduSubjectQueryWrapper);
        return subject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
