package com.atlisheng.eduuser.service;

import com.atlisheng.eduuser.entity.EduUser;
import com.atlisheng.eduuser.entity.bo.LoginUserInfoBo;
import com.atlisheng.eduuser.entity.vo.LoginVo;
import com.atlisheng.eduuser.entity.vo.RegisterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Earl
 * @since 2023-10-05
 */
public interface EduUserService extends IService<EduUser> {

    /**
     * @param loginVo
     * @return {@link String }
     * @描述 用户登录验证
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    String login(LoginVo loginVo);

    /**
     * @param registerVo
     * @描述 用户注册
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    void register(RegisterVo registerVo);

    /**
     * @param memberId
     * @return {@link LoginVo }
     * @描述 根据用户token获取用户登录信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    LoginUserInfoBo getLoginInfo(String memberId);

    /**
     * @param phoneNumber
     * @return boolean
     * @描述 验证手机号码是否可用
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    boolean verifyPhoneNumber(String phoneNumber);

    /**
     * @param smsCode
     * @return boolean
     * @描述 验证短信验证码是否匹配
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    boolean verifySmsCode(String phoneNumber,String smsCode);

    /**
     * @param openid
     * @return boolean
     * @描述 根据微信用户id判断该用户是否注册过
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/07
     * @since 1.0.0
     */
    EduUser getByOpenid(String openid);

    /**
     * @param userId
     * @return {@link Map }<{@link String }, {@link String }>
     * @描述 根据用户id获取用户信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/11
     * @since 1.0.0
     */
    Map<String, Object> getUserByUserId(String userId);

    /**
     * @param day
     * @return {@link Integer }
     * @描述 根据日期统计每日注册用户数量
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    Integer queryRegisterCountDaily(String day);
}
