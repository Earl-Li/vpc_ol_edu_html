package com.atlisheng.edustatistics.service.impl;

import com.atlisheng.edustatistics.client.UserClient;
import com.atlisheng.edustatistics.entity.EduStatistics;
import com.atlisheng.edustatistics.mapper.EduStatisticsMapper;
import com.atlisheng.edustatistics.service.EduStatisticsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站每日数据统计 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-10-13
 */
@Service
public class EduStatisticsServiceImpl extends ServiceImpl<EduStatisticsMapper, EduStatistics> implements EduStatisticsService {

    @Autowired
    private UserClient userClient;

    /**
     * @param day
     * @描述 通过日期生成当天的对应统计数据
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    public void generateDataByDate(String day) {
        //1.删除已存在的统计对象
        QueryWrapper<EduStatistics> dayQueryWrapper = new QueryWrapper<>();
        dayQueryWrapper.eq("date_calculated", day);
        baseMapper.delete(dayQueryWrapper);

        //2.调用各个服务获取统计信息
        //统计当天注册人数
        Integer registerNum = (Integer) userClient.queryRegisterCountDaily(day).getData().get("countRegister");
        //统计当天登录人数
        Integer loginNum = RandomUtils.nextInt(100, 200);//TODO
        //统计当天课程浏览数量
        Integer videoViewNum = RandomUtils.nextInt(100, 200);//TODO
        //统计当天课程发布数量
        Integer courseNum = RandomUtils.nextInt(100, 200);//TODO
        //统计当天课程售卖数量
        Integer sellNum= RandomUtils.nextInt(100,200);//TODO

        //创建统计对象
        EduStatistics dayData = new EduStatistics();
        dayData.setRegisterNum(registerNum);
        dayData.setLoginNum(loginNum);
        dayData.setVideoViewNum(videoViewNum);
        dayData.setCourseNum(courseNum);
        dayData.setDateCalculated(day);
        dayData.setSellNum(sellNum);
        baseMapper.insert(dayData);
    }

    /**
     * @param begin begin是开始日期【不带时分秒】
     * @param end end是结束日期【不带时分秒】
     * @param type type是前端需要的数据类型
     * @return {@link Map }<{@link String }, {@link Object }>
     * @描述 查询统计图表的数据，根据前端请求数据的种类，将数据和日期分别封装成两个list放入Map传给前端
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/14
     * @since 1.0.0
     */
    public Map<String, Object> queryChartData(String begin, String end, String type) {
        QueryWrapper<EduStatistics> dayQueryWrapper = new QueryWrapper<>();
        //选择要查询的字段
        dayQueryWrapper.select(type, "date_calculated");
        dayQueryWrapper.between("date_calculated", begin, end);
        List<EduStatistics> eduStatisticsList = baseMapper.selectList(dayQueryWrapper);
        Map<String, Object> map = new HashMap<>();
        List<Integer> dataList = new ArrayList<Integer>();
        List<String> dateList = new ArrayList<String>();
        map.put("dataList", dataList);
        map.put("dateList", dateList);
        //返回list会自动封装成数组形式，即json的数组形式，json有两种形式，对象到json对象，list集合会变成json数组形式
        for (int i = 0; i < eduStatisticsList.size(); i++) {
            EduStatistics eduStatistics = eduStatisticsList.get(i);
            dateList.add(eduStatistics.getDateCalculated());//把日期放入dateList
            switch (type) {
                case "register_num":
                    dataList.add(eduStatistics.getRegisterNum());
                    break;
                case "login_num":
                    dataList.add(eduStatistics.getLoginNum());
                    break;
                case "video_view_num":
                    dataList.add(eduStatistics.getVideoViewNum());
                    break;
                case "course_num":
                    dataList.add(eduStatistics.getCourseNum());
                    break;
                case "sell_num":
                    dataList.add(eduStatistics.getSellNum());
                    break;
                default:
                    break;
            }
        }
        return map;
    }
}
