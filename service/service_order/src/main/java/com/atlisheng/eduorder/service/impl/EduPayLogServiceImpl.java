package com.atlisheng.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atlisheng.eduorder.entity.EduOrder;
import com.atlisheng.eduorder.entity.EduPayLog;
import com.atlisheng.eduorder.mapper.EduPayLogMapper;
import com.atlisheng.eduorder.service.EduOrderService;
import com.atlisheng.eduorder.service.EduPayLogService;
import com.atlisheng.eduorder.utils.ConstantProperties;
import com.atlisheng.eduorder.utils.HttpClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
@Service
public class EduPayLogServiceImpl extends ServiceImpl<EduPayLogMapper, EduPayLog> implements EduPayLogService {

    @Autowired
    private EduOrderService eduOrderService;

    /**
     * @param orderNo
     * @return {@link Map }
     * @描述 生成微信支付二维码
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @Override
    public Map generateQRCode(String orderNo) {
        try {
            //根据订单id获取订单信息
            QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            EduOrder order = eduOrderService.getOne(wrapper);
            Map payParams = new HashMap();

            //1、设置支付参数，Map转成xml更加方便
            payParams.put("appid", ConstantProperties.APP_ID);
            payParams.put("mch_id", ConstantProperties.PARTNER);
            //这个是用微信的工具类，使每个生成的二维码都不一样，微信的更加完善
            payParams.put("nonce_str", WXPayUtil.generateNonceStr());
            //生成二维码的名称，这里填的是课程的名称
            payParams.put("body", order.getCourseTitle());
            //二维码中的唯一标识，这里使用的是订单号
            payParams.put("out_trade_no", orderNo);
            //固定写法，BigDecimal可以实现金钱的元角分，传入时要先变成long，在变成字符串，这里为什么要乘以100，可能因为是分，需要转成long类型先变成整数？
            payParams.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");
            //这是服务器地址，有域名相当于去掉www.后面的内容
            payParams.put("spbill_create_ip", "127.0.0.1");
            //这个是支付后做回调的地址，目前没有用到
            payParams.put("notify_url",ConstantProperties.NOTIFY_URL);
            //这个是支付类型，根据价格生成二维码
            payParams.put("trade_type", "NATIVE");

            //2、 HTTPClient来根据URL访问第三方接口并且传递参数，这个URL是固定由微信提供的
            //这个HTTPClient是自己写的工具类，这个工具类除了发送HttpClient请求外
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //client设置参数,需要用WXPayUtil.generateSignedXml方法把参数设置成xml格式，第二个参数是商户的key,weixin使用商户key对支付参数进行了加密处理让交易更加安全
            client.setXmlParam(WXPayUtil.generateSignedXml(payParams, ConstantProperties.PARTNER_KEY));
            //HttpClient默认不支持访问https，需要设置setHttps为true才支持
            client.setHttps(true);
            //使用client使用post方式发送请求
            client.post();

            //3、返回第三方的数据，返回的也是xml数据，需要微信工具使用xmlToMap把数据转换成Map格式
            String xml = client.getContent();
            //这里面的数据只有二维码地址，和result_code，前端还需要其他数据比如订单号、总价、课程ID，需要自己单独封装一个Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //4、封装返回结果集
            Map orderInfo = new HashMap<>();
            orderInfo.put("out_trade_no", orderNo);
            orderInfo.put("course_id", order.getCourseId());
            orderInfo.put("total_fee", order.getTotalFee());
            //返回二维码的操作状态，比如成功生成二维码就返回200
            orderInfo.put("result_code", resultMap.get("result_code"));
            //code_url是二维码的地址
            orderInfo.put("code_url", resultMap.get("code_url"));
            //微信支付二维码2小时过期，可采取2小时未支付取消订单
            //redisTemplate.opsForValue().set(orderNo, map, 120,TimeUnit.MINUTES);
            return orderInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * @param orderNo
     * @return {@link Map }<{@link String }, {@link String }>
     * @描述 根据订单号查询订单状态
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map queryPayStatusParams = new HashMap<>();
            queryPayStatusParams.put("appid", ConstantProperties.APP_ID);
            queryPayStatusParams.put("mch_id", ConstantProperties.PARTNER);
            queryPayStatusParams.put("out_trade_no", orderNo);
            queryPayStatusParams.put("nonce_str", WXPayUtil.generateNonceStr());

            //2、设置请求,这是查询微信订单支付的地址，和创建支付二维码的地址不同
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(queryPayStatusParams, ConstantProperties.PARTNER_KEY));
            client.setHttps(true);
            client.post();

            //3、返回第三方的数据
            String xml = client.getContent();

            //4、转成Map返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param orderInfo
     * @描述 从订单表和订单查询结果信息中获取订单信息并更新订单表状态和生成支付记录
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @Override
    public void updateOrderStatus(Map<String, String> orderInfo) {
        //获取订单号
        String orderNo = orderInfo.get("out_trade_no");
        //根据订单号查询订单信息
        QueryWrapper<EduOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        EduOrder order = eduOrderService.getOne(wrapper);
        //如果订单号状态已经是1了直接返回
        if(order.getStatus().intValue() == 1) return;
        //更改订单表的支付状态为1
        order.setStatus(1);
        eduOrderService.updateById(order);
        //记录支付日志
        EduPayLog payLog=new EduPayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());//订单完成支付的时间
        payLog.setPayType(1);//支付类型，微信支付
        payLog.setTotalFee(order.getTotalFee());//总金额(分)

        payLog.setTradeState(orderInfo.get("trade_state"));//支付状态,这个可以从查询支付信息的结果中获取
        payLog.setTransactionId(orderInfo.get("transaction_id"));//理解为订单的流水号，也可以从查询支付返回结果中获取
        payLog.setAttr(JSONObject.toJSONString(orderInfo));//订单的其他信息也能通过转换成json存入支付记录的其他属性字段中
        baseMapper.insert(payLog);//插入到支付日志表
    }
}
