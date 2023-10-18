package com.atlisheng.eduuser.service.impl;

import com.atlisheng.commonutils.encryption.MD5Util;
import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.eduuser.entity.EduUser;
import com.atlisheng.eduuser.entity.bo.LoginUserInfoBo;
import com.atlisheng.eduuser.entity.vo.LoginVo;
import com.atlisheng.eduuser.entity.vo.RegisterVo;
import com.atlisheng.eduuser.mapper.EduUserMapper;
import com.atlisheng.eduuser.service.EduUserService;
import com.atlisheng.eduuser.utils.ConstantProperties;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Earl
 * @since 2023-10-05
 */
@Service
public class EduUserServiceImpl extends ServiceImpl<EduUserMapper, EduUser> implements EduUserService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * @param loginVo
     * @return {@link String }
     * @描述 用户登录验证,校验用户手机号和密码是否为空串，验证用户手机号是否注册、密码是否正确，账号是否被禁用
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    @Override
    public String login(LoginVo loginVo) {
        String phoneNumber = loginVo.getPhoneNumber();
        String password = loginVo.getPassword();
        //校验参数,格式由前端校验
        if(StringUtils.isEmpty(phoneNumber)) {
            throw new CustomException(20001,"请输入您的手机号码");
        }
        if(StringUtils.isEmpty(password)){
            throw new CustomException(20001,"请输入您的登录密码");
        }
        //通过手机号码获取会员
        EduUser eduUser = baseMapper.selectOne(new QueryWrapper<EduUser>().eq("phone_number", phoneNumber));
        if(null == eduUser) {
            throw new CustomException(20001,"该手机号码尚未进行注册");
        }
        //校验密码
        if(!MD5Util.encrypt(password).equals(eduUser.getPassword())) {
            throw new CustomException(20001,"用户密码错误");
        }
        //校验是否被禁用
        if(eduUser.getIsDisabled()) {
            throw new CustomException(20001,"该账户已被禁用");
        }
        //到此用户登录信息校验成功，使用JWT生成token字符串
        String token = JwtUtils.getJwtToken(eduUser.getId(), eduUser.getNickname());
        return token;
    }

    /**
     * @param registerVo
     * @描述 用户注册，校验用户输入信息是否为空串、校验用户短信验证是否正确、校验手机号是否已经注册，校验通过将用户信息添加到数据库
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册信息，进行校验
        String nickname = registerVo.getNickname();
        String phoneNumber = registerVo.getPhoneNumber();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();
        //校验参数是否为空，实际前端会对格式进行校验，这里只是确认
        if(StringUtils.isEmpty(phoneNumber) ||
                StringUtils.isEmpty(phoneNumber) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(code)) {
            throw new CustomException(20001,"用户注册异常，请联系客服");
        }
        //从redis获取发送的验证码校验验证码
        String msmCode = redisTemplate.opsForValue().get(phoneNumber);
        if(!code.equals(msmCode)) {
            throw new CustomException(20001,"验证码错误");
        }
        //查询数据库中是否存在相同的手机号码
        Integer count = baseMapper.selectCount(new QueryWrapper<EduUser>().eq("phone_number", phoneNumber));
        //Integer的intValue方法判断对应的值，应该右自动类型转换吧
        if(count.intValue() > 0) {
            throw new CustomException(20001,"该手机号已被注册");
        }
        //添加注册信息到数据库
        EduUser eduUser = new EduUser();
        eduUser.setNickname(nickname);
        eduUser.setPhoneNumber(registerVo.getPhoneNumber());
        eduUser.setPassword(MD5Util.encrypt(password));
        eduUser.setIsDisabled(false);
        //设置用户默认头像
        eduUser.setAvatar(ConstantProperties.DEFAULT_AVATAR);
        save(eduUser);
    }

    /**
     * @param memberId
     * @return {@link LoginVo }
     * @描述 通过用户id获取用户信息并封装到登录信息中
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    @Override
    public LoginUserInfoBo getLoginInfo(String memberId) {
        EduUser eduUser = baseMapper.selectById(memberId);
        LoginUserInfoBo loginUserInfo = new LoginUserInfoBo();
        BeanUtils.copyProperties(eduUser, loginUserInfo);
        return loginUserInfo;
    }

    /**
     * @param phoneNumber
     * @return boolean
     * @描述 手机号码输入框失焦判断是否注册，true为以注册，false为未注册
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    @Override
    public boolean verifyPhoneNumber(String phoneNumber) {
        QueryWrapper<EduUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("phone_number",phoneNumber);
        System.out.println(count(queryWrapper));
        return count(queryWrapper)>0;
    }

    /**
     * @param phoneNumber
     * @param smsCode
     * @return boolean
     * @描述 验证短信验证码是否正确,正确返回true，错误返回false
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    @Override
    public boolean verifySmsCode(String phoneNumber, String smsCode) {
        return smsCode.equals(redisTemplate.opsForValue().get(phoneNumber));
    }

    /**
     * @param openid
     * @return boolean
     * @描述 根据用户微信id是否已经注册过,注册过返回false，没注册过返回true
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/07
     * @since 1.0.0
     */
    @Override
    public EduUser getByOpenid(String openid) {
        QueryWrapper<EduUser> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        return getOne(queryWrapper);
    }

    /**
     * @param userId
     * @return {@link Map }<{@link String }, {@link String }>
     * @描述 根据用户id获取用户信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/11
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> getUserByUserId(String userId) {
        Map<String,Object> userInfo=new HashMap<>();
        EduUser eduUser = getById(userId);
        userInfo.put("nickname",eduUser.getNickname());
        userInfo.put("avatar",eduUser.getAvatar());
        userInfo.put("phoneNumber",eduUser.getPhoneNumber());
        return userInfo;
    }

    /**
     * @param day
     * @return {@link Integer }
     * @描述 根据日期统计每日注册用户数量
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    @Override
    public Integer queryRegisterCountDaily(String day) {
        return baseMapper.queryRegisterCountDaily(day);
    }
}
