package com.atlisheng.eduorder.controller;


import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.entity.EduOrder;
import com.atlisheng.eduorder.service.EduOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
@RestController
@RequestMapping("/eduorder/order")

public class EduOrderController {
    @Autowired
    private EduOrderService eduOrderService;

    /**
     * @param courseId
     * @param request
     * @return {@link ResponseData }
     * @描述 根据课程id和用户id创建订单，返回订单id
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("createOrder/{courseId}")
    public ResponseData createOrder(@PathVariable String courseId, HttpServletRequest request) {
        String orderNo = eduOrderService.createOrder(courseId, JwtUtils.getMemberIdByJwtToken(request));
        return ResponseData.responseCall().data("orderNo", orderNo);
    }

    /**
     * @param orderNo
     * @return {@link ResponseData }
     * @描述 根据订单号查询订单信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("queryOrder/{orderNo}")
    public ResponseData queryOrderByOrderNo(@PathVariable String orderNo) {
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        EduOrder order = eduOrderService.getOne(wrapper);
        return ResponseData.responseCall().data("order", order);
    }

    /**
     * @param userId
     * @param courseId
     * @return boolean
     * @描述 有订单状态是1，用户名和课程id匹配的记录则表示已经支付，否则未支付，这个接口值提供给课程信息查询服务调用
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("confirmCourseBuyStatus/{userId}/{courseId}")
    public boolean confirmCourseBuyStatus(@PathVariable String userId, @PathVariable String courseId) {
        int count = eduOrderService.count(new QueryWrapper<EduOrder>().eq("user_id", userId).eq("course_id", courseId).eq("status", 1));
        return count>0 ;
    }

}

