package com.atlisheng.edustatistics.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.edustatistics.service.impl.EduStatisticsServiceImpl;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站每日数据统计 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-10-13
 */
@RestController
@RequestMapping("/edustatistics/statistics")

public class EduStatisticsController {

    @Autowired
    EduStatisticsServiceImpl eduStatisticsService;

    /**
     * @param day
     * @return {@link ResponseData }
     * @描述 按日期生成数据统计记录
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/14
     * @since 1.0.0
     */
    @PostMapping("generateDataByDate/{day}")
    public ResponseData generateDataByDate(@PathVariable String day) {
        eduStatisticsService.generateDataByDate(day);
        return ResponseData.responseCall();
    }

    /**
     * @param begin
     * @param end
     * @param type
     * @return {@link ResponseData }
     * @描述 查询前端统计图表展示的数据
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/14
     * @since 1.0.0
     */
    @GetMapping("dataDisplayByChart/{begin}/{end}/{type}")
    public ResponseData dataDisplayByChart(@PathVariable String begin,
                                           @PathVariable String end,
                                           @PathVariable String type){
        Map<String, Object> map = eduStatisticsService.queryChartData(begin, end, type);
        return ResponseData.responseCall().data(map);
    }

}

