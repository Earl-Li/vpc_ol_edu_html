package com.atlisheng.eduuser.controller;


import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.eduuser.entity.bo.LoginUserInfoBo;
import com.atlisheng.eduuser.entity.vo.LoginVo;
import com.atlisheng.eduuser.entity.vo.RegisterVo;
import com.atlisheng.eduuser.service.EduUserService;
import com.atlisheng.servicebase.exceptions.CustomException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Earl
 * @since 2023-10-05
 */
@RestController
@RequestMapping("/eduuser/ucenter")

@Api(description = "用户状态控制中心")
public class EduUserController {
    @Autowired
    private EduUserService userService;

    @ApiOperation(value = "用户登录")
    @PostMapping("login")
    public ResponseData login(@ApiParam(name = "loginInfo",value = "用户登录信息",required = true) @RequestBody LoginVo loginVo) {
        String token = userService.login(loginVo);
        return ResponseData.responseCall().data("token", token);
    }

    @ApiOperation(value = "用户注册")
    @PostMapping("register")
    public ResponseData register(@ApiParam(name = "registerInfo",value = "用户注册信息",required = true)@RequestBody RegisterVo registerVo){
        userService.register(registerVo);
        return ResponseData.responseCall().message("注册成功");
    }

    /**
     * @param request
     * @return {@link ResponseData }
     * @描述 根据token获取用户id,通过用户id查询用户数据
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/05
     * @since 1.0.0
     */
    @ApiOperation(value = "根据token获取登录信息")
    @GetMapping("getLoginInfo")
    public ResponseData getLoginInfo(@ApiParam(name = "request",value = "用户请求",required = true) HttpServletRequest request){
        try {
            //从请求中获取token，并将token解析成用户id进行返回，如果没有token就返回空串
            String userId = JwtUtils.getMemberIdByJwtToken(request);
            LoginUserInfoBo loginUserInfo = userService.getLoginInfo(userId);
            return ResponseData.responseCall().data("loginInfo", loginUserInfo);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(20001,"请先登录");
        }
    }

    /**
     * @param phoneNumber
     * @return {@link ResponseData }
     * @描述 已注册返回message"该号码已被注册"，未注册message返回空串
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    @ApiOperation("验证手机号是否已被注册")
    @GetMapping("verifyPhoneNumber/{phoneNumber}")
    public ResponseData verifyPhoneNumber(@ApiParam(name = "phoneNumber",value = "用户手机号码",required = true) @PathVariable String phoneNumber){
        return userService.verifyPhoneNumber(phoneNumber)?ResponseData.responseCall().message("该号码已被注册"):ResponseData.responseCall().message("");
    }

    /**
     * @param smsCode
     * @param phoneNumber
     * @return {@link ResponseData }
     * @描述 匹配正确返回message空串,错误返回message"短信验证码错误"
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/06
     * @since 1.0.0
     */
    @ApiOperation("验证短信验证码是否匹配")
    @GetMapping("verifySmsCode/{smsCode}/{phoneNumber}")
    public ResponseData verifySmsCode(@ApiParam(name = "smsCode",value = "手机验证码",required = true) @PathVariable String smsCode,
                                      @ApiParam(name = "phoneNumber",value = "用户手机号码",required = true) @PathVariable String phoneNumber){
        return userService.verifySmsCode(smsCode,phoneNumber)?ResponseData.responseCall().message(""):ResponseData.responseCall().message("短信验证码错误");
    }

    /**
     * @param userId
     * @return {@link ResponseData }
     * @描述 根据用户id获取用户信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/11
     * @since 1.0.0
     */
    @ApiOperation("根据用户id获取用户信息")
    @GetMapping("getUserByUserId/{userId}")
    public ResponseData getUserByUserId(@ApiParam(name = "userId",value = "用户ID",required = true) @PathVariable String userId){
        Map<String,Object> userInfo=userService.getUserByUserId(userId);
        return ResponseData.responseCall().data(userInfo);
    }

    /**
     * @param day
     * @return {@link ResponseData }
     * @描述 根据日期统计每日注册用户数量
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/13
     * @since 1.0.0
     */
    @GetMapping(value = "queryRegisterCountDaily/{day}")
    public ResponseData queryRegisterCountDaily(
            @PathVariable String day){
        Integer registerCount = userService.queryRegisterCountDaily(day);
        return ResponseData.responseCall().data("registerCount", registerCount);
    }

}

