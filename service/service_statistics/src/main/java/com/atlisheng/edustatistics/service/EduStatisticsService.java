package com.atlisheng.edustatistics.service;

import com.atlisheng.edustatistics.entity.EduStatistics;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站每日数据统计 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-10-13
 */
public interface EduStatisticsService extends IService<EduStatistics> {

    public void generateDataByDate(String day);

    /**
     * @param begin
     * @param end
     * @param type
     * @return {@link Map }<{@link String }, {@link Object }>
     * @描述 查询统计图表的数据
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/14
     * @since 1.0.0
     */
    public Map<String, Object> queryChartData(String begin, String end, String type);
}
