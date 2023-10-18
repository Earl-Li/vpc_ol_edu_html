package com.atlisheng.eduuser.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 用户登录的vo类
 * @创建日期 2023/10/05
 * @since 1.0.0
 */
@Data
@ApiModel(value="用户登录信息对象",description = "封装用户登录信息")
public class LoginVo {
    @ApiModelProperty(value = "注册手机号码",example = "18794830715")
    private String phoneNumber;
    @ApiModelProperty(value = "用户密码",example = "123456")
    private String password;
}
