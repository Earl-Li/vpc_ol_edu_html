package com.atlisheng.edusms.controller;

import com.atlisheng.commonutils.response.ResponseData;
import com.atlisheng.edusms.service.SmsService;
import com.atlisheng.edusms.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("edusms/confirm")

@Api(description = "短信验证服务")
public class SmsController {
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping(value = "/send/{phoneNumber}")
    @ApiOperation("发送短信验证码并存入redis中")
    public ResponseData code(@ApiParam(name="phoneNumber",value = "手机号码",required = true,defaultValue = "18794830715")
                                 @PathVariable String phoneNumber) {
        //通过手机号从redis中获取验证码,opsForValue().get()就是从redis获取参数的方法,验证redis一分钟时效，时效性小于四分钟就可以再次发送
        String code = redisTemplate.opsForValue().get(phoneNumber);
        //redisTemplate.getExpire(phoneNumber,TimeUnit.MINUTES)是获取有效时间
        if((!StringUtils.isEmpty(code)) & redisTemplate.getExpire(phoneNumber,TimeUnit.MINUTES)>=4)
            return ResponseData.responseErrorCall().message("请您在"+redisTemplate.getExpire(phoneNumber,TimeUnit.SECONDS)%60+"秒后进行尝试!");
        code = RandomUtil.getSixBitRandom();
        Map<String,Object> param = new HashMap<>();
        param.put("code", code);
        boolean isSend = smsService.sendMessage(phoneNumber, param);
        if(isSend) {
            //设置的5分钟的有效时长，TimeUnit.MINUTES是一分钟，5是5个一分钟
            redisTemplate.opsForValue().set(phoneNumber, code,5, TimeUnit.MINUTES);
            return ResponseData.responseCall();
        } else {
            return ResponseData.responseErrorCall().message("发送短信失败");
        }
    }
}
