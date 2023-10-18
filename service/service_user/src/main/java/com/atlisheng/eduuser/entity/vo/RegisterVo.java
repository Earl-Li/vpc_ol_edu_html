package com.atlisheng.eduuser.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 用户注册vo类
 * @创建日期 2023/10/05
 * @since 1.0.0
 */
@Data
@ApiModel(value = "用户注册信息对象",description = "封装用户注册信息")
public class RegisterVo {
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "手机号码")
    private String phoneNumber;
    @ApiModelProperty(value = "用户密码")
    private String password;
    @ApiModelProperty(value = "短信验证码")
    private String code;
}
