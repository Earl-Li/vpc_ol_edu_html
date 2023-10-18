package com.atlisheng.eduorder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 订单工具类
 * @创建日期 2023/10/12
 * @since 1.0.0
 */
public class OrderUtil {
    /**
     * @return {@link String }
     * @描述 生成订单号,实际使用的mp的算法生成的订单号
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    public static String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;
    }
}
