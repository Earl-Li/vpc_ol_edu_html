package com.atlisheng.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atlisheng.eduservice.entity.EduSubject;
import com.atlisheng.eduservice.entity.excel.SubjectData;
import com.atlisheng.eduservice.entity.bo.subject.FirstLevelSubject;
import com.atlisheng.eduservice.entity.bo.subject.SecondLevelSubject;
import com.atlisheng.eduservice.listener.SubjectExcelListener;
import com.atlisheng.eduservice.mapper.EduSubjectMapper;
import com.atlisheng.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-09-10
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    /**
     * @param file
     * @描述 添加课程分类
     * 注意在service中new了一个SubjectExcelListener对象，该对象没有交给Spring进行管理，
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/10
     * @since 1.0.0
     */
    @Override
    public void saveSubject(MultipartFile file,EduSubjectService eduSubjectService) {
        try {
            InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, SubjectData.class,new SubjectExcelListener(eduSubjectService)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return {@link List }<{@link FirstLevelSubject }>
     * @描述 从数据库获取所有的课程信息，并按照一级、二级分类、三级分类将课程封装成list集合并返回给前端
     * 这个是自定义查询方法，以前依靠mp查询数据库是直接通过service继承来的方案在controller中进行调用，现在在service中自定义数据处理方法需要在
     * service中自己对自己的方法进行调用，mp在ServiceImpl中自动注入了baseMapper,可以通过baseMapper调用相应的方法，能否通过this达到一样的效果？
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/11
     * @since 1.0.0
     */
    @Override
    public List<FirstLevelSubject> getAllSubject() {
        //查询一级课程分类，wrapper能清除条件反复使用吗？
        QueryWrapper<EduSubject> firstLevelSubjectQueryWrapper = new QueryWrapper<>();
        firstLevelSubjectQueryWrapper.eq("parent_id","0");
        List<EduSubject> firstLevelSubjects = list(firstLevelSubjectQueryWrapper);
        //也可以使用自动注入的baseMapper直接调用selectList方法查询所有，实际ServiceImpl对selectList方法进行了封装

        //查询二级课程分类，考虑到根据一级课程分类的id对二级课程分类多次查询效率较低，直接一次性查询所有的二级分类后再进行统一封装
        QueryWrapper<EduSubject> secondLevelSubjectQueryWrapper = new QueryWrapper<>();
        secondLevelSubjectQueryWrapper.ne("parent_id","0");
        List<EduSubject> secondLevelSubjects = list(secondLevelSubjectQueryWrapper);

        //创建list集合，用于最终封装数据
        List<FirstLevelSubject> finalSubjectList=new ArrayList<>();
        //创建HashMap，便于封装二级分类
        Map<String,List<SecondLevelSubject>> classificationMap=new HashMap<>();
        //封装一级分类
        //把查询出来的一级分类遍历并读取id和title信息进行封装，spring框架提供一个工具类BeanUtils，其中的copyProperties方法会将第一个参数
        // 对象的属性值搞出来放在第二个参数对象属性上，避免属性过多代码繁琐
        firstLevelSubjects.forEach(eduSubject -> {
            FirstLevelSubject firstLevelSubject = new FirstLevelSubject();
            //BeanUtils的copyProperties方法作用是把eduSubject的属性值复制到firstLevelSubject中去，第二个对象中没有的值就不进行封装
            BeanUtils.copyProperties(eduSubject,firstLevelSubject);
            finalSubjectList.add(firstLevelSubject);
            classificationMap.put(eduSubject.getId(),new ArrayList<>());
        });
        secondLevelSubjects.forEach(eduSubject -> {
            SecondLevelSubject secondLevelSubject = new SecondLevelSubject();
            BeanUtils.copyProperties(eduSubject,secondLevelSubject);
            classificationMap.get(eduSubject.getParentId()).add(secondLevelSubject);
        });
        //将二级分类拷贝到一级分类的children属性中
        finalSubjectList.forEach(firstLevelSubject -> {
            firstLevelSubject.setChildren(classificationMap.get(firstLevelSubject.getId()));
        });
        return finalSubjectList;
    }
}
