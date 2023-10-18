package com.atlisheng.eduuser.entity.bo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class LoginUserInfoBo {
    private String id;

    private String openid;

    private String phoneNumber;

    private String nickname;

    private Integer sex;

    private Integer age;

    private String avatar;

    private String sign;

    private Date gmtCreate;

}
