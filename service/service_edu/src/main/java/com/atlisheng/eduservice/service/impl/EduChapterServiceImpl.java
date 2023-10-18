package com.atlisheng.eduservice.service.impl;

import com.atlisheng.eduservice.entity.EduChapter;
import com.atlisheng.eduservice.entity.EduCourse;
import com.atlisheng.eduservice.entity.EduSubject;
import com.atlisheng.eduservice.entity.EduVideo;
import com.atlisheng.eduservice.entity.bo.chapter.Chapter;
import com.atlisheng.eduservice.entity.bo.chapter.Section;
import com.atlisheng.eduservice.entity.bo.subject.FirstLevelSubject;
import com.atlisheng.eduservice.entity.bo.subject.SecondLevelSubject;
import com.atlisheng.eduservice.mapper.EduChapterMapper;
import com.atlisheng.eduservice.service.EduChapterService;
import com.atlisheng.eduservice.service.EduVideoService;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-09-11
 */
@Service
public class EduChapterServiceImpl extends ServiceImpl<EduChapterMapper, EduChapter> implements EduChapterService {

    @Autowired
    private EduVideoService eduVideoService;

    @Override
    public List<Chapter> getChaptersByCourseId(String courseId) {
        //通过课程ID查询所有章节信息
        QueryWrapper<EduChapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id",courseId);
        List<EduChapter> eduChapterList = list(chapterQueryWrapper);
        //也可以使用自动注入的baseMapper直接调用selectList方法查询所有，实际ServiceImpl的list方法对selectList方法进行了封装

        //查询课程对应的小节信息，考虑到根据章节id对小节分类多次查询效率较低，直接一次性查询所有的小节后再进行统一封装
        QueryWrapper<EduVideo> sectionQueryWrapper = new QueryWrapper<>();
        sectionQueryWrapper.eq("course_id",courseId);
        List<EduVideo> eduVideoList = eduVideoService.list(sectionQueryWrapper);

        //创建list集合，用于最终封装数据
        List<Chapter> finalChapterList=new ArrayList<>();
        //创建HashMap，便于封装小节信息
        Map<String,List<Section>> classificationMap=new HashMap<>();
        //封装章节信息
        //把查询出来的章节遍历并读取id和title信息进行封装，spring框架提供一个工具类BeanUtils，其中的copyProperties方法会将第一个参数对象的属性值搞出来放在第二个参数对象属性上，避免属性过多代码繁琐
        eduChapterList.forEach(eduChapter -> {
            Chapter chapter = new Chapter();
            BeanUtils.copyProperties(eduChapter,chapter);
            finalChapterList.add(chapter);
            classificationMap.put(eduChapter.getId(),new ArrayList<>());
        });
        eduVideoList.forEach(eduVideo -> {
            Section section = new Section();
            BeanUtils.copyProperties(eduVideo,section);
            classificationMap.get(eduVideo.getChapterId()).add(section);
        });
        //将二级分类拷贝到一级分类的children属性中
        finalChapterList.forEach(chapter -> {
            chapter.setChildren(classificationMap.get(chapter.getId()));
        });
        return finalChapterList;
    }

    /**
     * @param chapterId
     * @return boolean
     * @描述 查询数据库对应章节下是否存在小节，不存在就直接删除章节，存在就提示该章节下存在小节，无法删除该章节
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/22
     * @since 1.0.0
     */
    @Override
    public boolean deleteChapter(String chapterId) {
        QueryWrapper<EduVideo> eduVideoQueryWrapper = new QueryWrapper<>();
        eduVideoQueryWrapper.eq("chapter_id",chapterId);
        if (eduVideoService.count(eduVideoQueryWrapper)>0){
            throw new CustomException(20001,"该章节下存在小节，无法删除该章节");
        }
        return removeById(chapterId);
    }

    /**
     * @param courseId
     * @return boolean
     * @描述 根据课程id删除课程下的所有章节
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/09/25
     * @since 1.0.0
     */
    @Override
    public boolean removeByCourseId(String courseId) {
        QueryWrapper<EduChapter> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("course_id",courseId);
        return remove(queryWrapper);
    }
}
