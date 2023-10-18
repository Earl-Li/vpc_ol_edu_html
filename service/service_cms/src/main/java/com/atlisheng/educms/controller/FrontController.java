package com.atlisheng.educms.controller;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.educms.client.EduServiceClient;
import com.atlisheng.educms.entity.EduBanner;
import com.atlisheng.educms.service.EduBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/educms/front")

@Api("前台数据管理")
public class FrontController {
    @Autowired
    private EduBannerService bannerService;
    @Autowired
    private EduServiceClient eduServiceClient;

    /**
     * @return {@link ResponseData }
     * @描述 获取数据库中的所有banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "获取首页banner")
    @GetMapping("getAllBanner")
    public ResponseData getAllBanner() {
        List<EduBanner> bannerList = bannerService.queryBannerList();
        return ResponseData.responseCall().data("bannerList", bannerList);
    }

    /**
     * @return {@link ResponseData }
     * @描述 获取最受欢迎的八门课程
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @GetMapping("getPopularCourse")
    @ApiOperation("获取最受欢迎的八门课程")
    @Cacheable(value = "indexData",key="'hotCourseList'")
    public ResponseData getPopularCourse(){
        return eduServiceClient.queryPopularCourse();
    }

    /**
     * @return {@link ResponseData }
     * @描述 获取最高资历的四位讲师
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/03
     * @since 1.0.0
     */
    @GetMapping("getPopularTeacher")
    @ApiOperation("获取最高资历的四位讲师")
    @Cacheable(value = "indexData",key="'TeacherList'")
    public ResponseData getPopularTeacher(){
        return eduServiceClient.queryPopularTeacher();
    }

}
