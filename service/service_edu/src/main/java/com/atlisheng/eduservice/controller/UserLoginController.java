package com.atlisheng.eduservice.controller;

import com.atlisheng.commonutils.response.ResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 用于用户登录的接口入口
 * @创建日期 2023/08/30
 * @since 1.0.0
 */
@RestController
@RequestMapping("/eduservice/user")

@Api(description = "用户登录管理")
public class UserLoginController {
    /**
     * @return {@link ResponseData }
     * @描述 用户登录操作服务器端方法
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/30
     * @since 1.0.0
     */
    @PostMapping("login")
    @ApiOperation("用户登录验证")
    public ResponseData userLogin(){
        return ResponseData.responseCall().data("token","admin");//admin是用户名，后面涉及查表再改
    }

    /**
     * @return {@link ResponseData }
     * @描述 获取用户信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/08/30
     * @since 1.0.0
     */
    @GetMapping("info")
    @ApiOperation("获取用户信息")
    public ResponseData getUserInfo(){
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("roles","[admin]");//这是啥意思
        userInfo.put("name","admin");
        userInfo.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return ResponseData.responseCall().data(userInfo);
    }

}
