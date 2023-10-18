package com.atlisheng.eduorder.service.impl;

import com.atlisheng.commonutils.entity.vo.CourseInfoForm;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduorder.client.EduClient;
import com.atlisheng.eduorder.client.UserClient;
import com.atlisheng.eduorder.entity.EduOrder;
import com.atlisheng.eduorder.mapper.EduOrderMapper;
import com.atlisheng.eduorder.service.EduOrderService;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-10-12
 */
@Service
public class EduOrderServiceImpl extends ServiceImpl<EduOrderMapper, EduOrder> implements EduOrderService {

    @Autowired
    private EduClient eduClient;
    @Autowired
    private UserClient userClient;

    /**
     * @param courseId
     * @param userId
     * @return {@link String }
     * @描述 根据课程信息、用户信息、讲师信息创建订单，返回订单号
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/12
     * @since 1.0.0
     */
    @Override
    public String createOrder(String courseId, String userId) {
        //远程调用课程服务，根据课程id获取课程信息
        CourseInfoForm courseInfo = eduClient.queryCourseInfoById(courseId);
        //远程调用用户服务，根据用户id获取用户信息
        ResponseData  userResponse= userClient.getLoginInfo(userId);
        Map<String, Object> userInfo = userResponse.getData();
        //获取讲师信息，讲师名字
        String teacherName = (String)eduClient.queryTeacherNameById(courseInfo.getTeacherId()).getData().get("teacherName");
        //创建订单对象，这个对象直接保存在数据库中，通过订单号查询
        EduOrder order = new EduOrder();
        //生成订单号直接用mp中的,貌似是雪花算法生成的，老师的是根据时间和随机数生成的，两个后续都可以研究一下
        //order.setOrderNo(OrderUtil.generateOrderNo());
        //mp生成订单号，确认一下是不是雪花算法
        order.setOrderNo(IdWorker.getIdStr());
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfo.getTitle());
        order.setCourseCover(courseInfo.getCover());
        order.setTeacherName(teacherName);
        order.setTotalFee(courseInfo.getPrice());
        order.setUserId(userId);
        order.setPhoneNumber((String) userInfo.get("phoneNumber"));
        order.setNickname((String) userInfo.get("nickname"));
        order.setStatus(0);//默认订单状态是未支付
        order.setPayType(1);//支付类型默认是1，即微信支付
        baseMapper.insert(order);
        return order.getOrderNo();
    }
}
