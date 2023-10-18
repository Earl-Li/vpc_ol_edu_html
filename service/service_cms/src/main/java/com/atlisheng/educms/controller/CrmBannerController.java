package com.atlisheng.educms.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.educms.entity.EduBanner;
import com.atlisheng.educms.service.EduBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 后台管理banner表 前端控制器
 * CRM是客户关系管理系统
 * </p>
 *
 * @author Earl
 * @since 2023-10-02
 */
@RestController
@RequestMapping("/educms/bannercrm")

@MapperScan("com.atlisheng.educms.mapper")
@Api("后台Banner管理系统")
public class CrmBannerController {
    @Autowired
    private EduBannerService bannerService;

    /**
     * @param page
     * @param limit
     * @return {@link ResponseData }
     * @描述 带条件分页查询banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "获取Banner分页列表")
    @GetMapping("{page}/{limit}")
    public ResponseData pageFactorBanner(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<EduBanner> pageParam = new Page<>(page, limit);
        bannerService.pageBanner(pageParam,null);
        return ResponseData.responseCall().data("banners", pageParam.getRecords()).data("total",pageParam.getTotal());
    }

    /**
     * @param id
     * @return {@link ResponseData }
     * @描述 根据bannerId获取banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "通过Id获取Banner")
    @GetMapping("get/{id}")
    public ResponseData getBannerById(@ApiParam(name="bannerId",value = "ID",required = true) @PathVariable String id) {
        EduBanner banner = bannerService.getBannerById(id);
        return ResponseData.responseCall().data("banner", banner);
    }

    /**
     * @param banner
     * @return {@link ResponseData }
     * @描述 通过实体对象增添banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "新增Banner")
    @PostMapping("add")
    public ResponseData addBanner(@ApiParam(name="banner",value = "Banner对象",required = true) @RequestBody EduBanner banner) {
        bannerService.addBanner(banner);
        return ResponseData.responseCall();
    }

    /**
     * @param banner
     * @return {@link ResponseData }
     * @描述 根据bannerId修改Banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "根据bannerId修改Banner")
    @PutMapping("update")
    public ResponseData updateBannerById(@ApiParam(name="bannerId",value = "ID",required = true) @RequestBody EduBanner banner) {
        bannerService.updateBannerById(banner);
        return ResponseData.responseCall();
    }

    /**
     * @param id
     * @return {@link ResponseData }
     * @描述 根据bannerId删除Banner
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/02
     * @since 1.0.0
     */
    @ApiOperation(value = "根据bannerId删除Banner")
    @DeleteMapping("remove/{id}")
    public ResponseData removeBannerById(@ApiParam(name="bannerId",value = "ID",required = true) @PathVariable String id) {
        bannerService.removeBannerById(id);
        return ResponseData.responseCall();
    }
}

