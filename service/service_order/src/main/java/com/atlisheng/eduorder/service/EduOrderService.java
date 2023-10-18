package com.atlisheng.eduorder.service;

import com.atlisheng.eduorder.entity.EduOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
public interface EduOrderService extends IService<EduOrder> {

    /**
     * @param courseId
     * @param userId
     * @return {@link String }
     * @描述 创建课程订单，远程调用edu和user服务，查询课程和用户信息创建订单并返回课程id
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    String createOrder(String courseId, String userId);

}
