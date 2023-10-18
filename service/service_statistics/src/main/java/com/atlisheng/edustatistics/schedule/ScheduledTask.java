package com.atlisheng.edustatistics.schedule;

import com.atlisheng.edustatistics.service.EduStatisticsService;
import com.atlisheng.edustatistics.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ScheduledTask {
    @Autowired
    private EduStatisticsService eduStatisticsService;

    /**
     * @描述 测试定时任务用 每天七点到二十三点每五秒执行一次
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    public void task1() {
        System.out.println("*********++++++++++++*****执行了");
    }

    /**
     * @描述 每天凌晨1点执行定时任务，得到昨天的满足数据库日期部分格式的昨天日期，获取数据
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyData() {
        //获取上一天的日期
        String day = DateUtil.formatDate(DateUtil.addDays(new Date(), -1));
        eduStatisticsService.generateDataByDate(day);
    }
}

