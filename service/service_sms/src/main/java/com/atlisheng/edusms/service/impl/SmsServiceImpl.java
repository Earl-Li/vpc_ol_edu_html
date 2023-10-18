package com.atlisheng.edusms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atlisheng.edusms.service.SmsService;
import com.atlisheng.edusms.utils.ConstantProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {

    /**
     * @param phoneNumber
     * @param param
     * @return boolean
     * @描述 阿里云发送短信服务
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    @Override
    public boolean sendMessage(String phoneNumber, Map<String, Object> param) {
        if(StringUtils.isEmpty(phoneNumber)) return false;
        //default是地域节点，直接default
        DefaultProfile profile = DefaultProfile.getProfile(
                "default",
                ConstantProperties.KEY_ID,
                ConstantProperties.ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        //这部分参数是固定写法
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        //请求提交方式
        request.setMethod(MethodType.POST);
        //请求访问的服务器
        request.setDomain("dysmsapi.aliyuncs.com");
        //版本号
        request.setVersion("2017-05-25");
        //行为为发送短信
        request.setAction("SendSms");

        //设置参数
        request.putQueryParameter("PhoneNumbers", phoneNumber);//设置手机号，这个key是固定的
        request.putQueryParameter("SignName", "阿里云短信测试");//设置阿里云的签名名称
        request.putQueryParameter("TemplateCode", ConstantProperties.TEMPLATE_CODE);//模板code
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(param));//这里面有code，这里需要传递的也是json格式，Map可以直接变成json格式
        try {
            CommonResponse response = client.getCommonResponse(request);//这个就是发送请求的方法
            return response.getHttpResponse().isSuccess();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;//捕获异常就返回false
    }
}
