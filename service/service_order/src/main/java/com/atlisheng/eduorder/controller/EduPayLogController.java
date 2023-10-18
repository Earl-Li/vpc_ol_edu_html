package com.atlisheng.eduorder.controller;


import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.EduClient;
import com.atlisheng.eduorder.entity.EduOrder;
import com.atlisheng.eduorder.service.EduOrderService;
import com.atlisheng.eduorder.service.EduPayLogService;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
@RestController
@RequestMapping("/eduorder/paylog")

public class EduPayLogController {
    @Autowired
    private EduOrderService eduOrderService;
    @Autowired
    private EduPayLogService eduPayLogService;
    @Autowired
    private EduClient eduClient;

    /**
     * @param orderNo
     * @return {@link ResponseData }
     * @描述 生成微信支付二维码
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("/generateQRCode/{orderNo}")
    public ResponseData generateQRCode(@PathVariable String orderNo) {
        //返回的信息中包含二维码地址和其他返回信息，使用Map进行封装
        Map payInfo = eduPayLogService.generateQRCode(orderNo);
        System.out.println("******二维码*****"+payInfo);
        return ResponseData.responseCall().data(payInfo);
    }

    /**
     * @param orderNo
     * @return {@link ResponseData }
     * @描述 根据订单号查询订单支付状态
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @GetMapping("/queryPayStatus/{orderNo}")
    public ResponseData queryPayStatus(@PathVariable String orderNo) {
        //调用查询接口，也是访问一个地址，转换参数xml和Map
        Map<String, String> payStatus = eduPayLogService.queryPayStatus(orderNo);
        System.out.println("=====支付状态====="+payStatus);
        System.out.println();
        if (payStatus == null) {//返回的map为空，此时一般是微信支付出了问题,28004对应前端响应拦截器中的支付出错
            return ResponseData.responseErrorCall().code(28004).message("支付出错了");
        }
        if (payStatus.get("trade_state").equals("SUCCESS")) {//如果trade_state=SUCCESS表示订单支付成功
            //添加记录到支付记录中并更改订单状态
            eduPayLogService.updateOrderStatus(payStatus);
            //调用edu中的course相关服务，把course的购买数量加1
            QueryWrapper<EduOrder> eduOrderQueryWrapper=new QueryWrapper<EduOrder>().eq("order_no",orderNo);
            EduOrder eduOrder = eduOrderService.getOne(eduOrderQueryWrapper);
            try {
                if (eduClient.updateCourseBuyCount(eduOrder.getCourseId()).getCode() == 20001) {
                    throw new CustomException(20001,"课程购买数量更新失败");
                }
            }catch (CustomException e){
                e.printStackTrace();
            }
            //20000对应的是前端响应拦截器中状态码20000
            return ResponseData.responseCall().message("支付成功");
        }
        //和前端响应拦截器是对应的，25000对应的是订单支付中，订单支付中不做任何提示，注意响应成功也可以设置状态码
        return ResponseData.responseCall().code(25000).message("支付中");
    }

}

