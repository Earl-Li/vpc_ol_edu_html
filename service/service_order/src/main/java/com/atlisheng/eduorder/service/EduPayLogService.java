package com.atlisheng.eduorder.service;

import com.atlisheng.eduorder.entity.EduPayLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
public interface EduPayLogService extends IService<EduPayLog> {
    /**
     * @param orderNo
     * @return {@link Map }
     * @描述 通过订单号生成二维码
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    Map generateQRCode(String orderNo);

    /**
     * @param orderNo
     * @return {@link Map }<{@link String }, {@link String }>
     * @描述 根据订单号查询订单状态
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    Map<String, String> queryPayStatus(String orderNo);

    /**
     * @param map
     * @描述 更新订单状态
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    void updateOrderStatus(Map<String, String> map);
}
