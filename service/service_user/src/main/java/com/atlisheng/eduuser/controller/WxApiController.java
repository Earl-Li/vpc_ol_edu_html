package com.atlisheng.eduuser.controller;

import com.atlisheng.commonutils.jwt.JwtUtils;
import com.atlisheng.eduuser.entity.EduUser;
import com.atlisheng.eduuser.service.EduUserService;
import com.atlisheng.eduuser.utils.ConstantProperties;
import com.atlisheng.eduuser.utils.HttpClientUtil;
import com.atlisheng.servicebase.exceptions.CustomException;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Api(description = "微信扫码登录接口")
@Controller

@RequestMapping("/api/ucenter/wx")
public class WxApiController {
    @Autowired
    EduUserService eduUserService;
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("callback")
    public String callback(String code,String state){
        //获取扫码人的信息，调用注册方法
        //得到授权临时票据code,类似于短信验证码
        //从redis中将state获取出来，和当前传入的state作比较，做网站防攻击校验，这个好像要自己实现
        if(StringUtils.isEmpty(state) || !(state.equals(redisTemplate.opsForValue().get("state")))){
            throw new CustomException(20001,"会话已过期，请重新登录");
        }
        //如果一致则放行，如果不一致则抛出异常：非法访问
        //向认证服务器https://api.weixin.qq.com/sns/oauth2/access_token发送请求携带
        // appid[网站id]、secret[网站密钥]、code[授权临时票据]换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                        "?appid=%s" +
                        "&secret=%s" +
                        "&code=%s" +
                        "&grant_type=authorization_code";
        //拼接对应的三个参数
        String accessTokenUrl = String.format(baseAccessTokenUrl,
                ConstantProperties.WX_OPEN_APP_ID,
                ConstantProperties.WX_OPEN_APP_SECRET,
                code);

        //用httpClient发送请求，这个技术很古老，但是一直在使用，得到对应返回的token和微信用户id，不用浏览器也能实现浏览器发送请求的效果
        String result = null;
        try {
            //自定义Util包的get方法，是获取token和用户微信id的方法，这玩意儿会直接返回响应体，并转换成json格式的字符串
            result = HttpClientUtil.get(accessTokenUrl);
            System.out.println("accessToken=============" + result);
        } catch (Exception e) {
            throw new CustomException(20001, "获取access_token失败");
        }

        //解析json字符串，获取access_token和openid，老办法是分割逗号，取第一个和第四个，这种方式不好；也可以根据一段长度的字符串进行匹配
        //方便直接用谷歌提供的gson，直接把json格式字符串转换成Map的key-value形式，直接通过key从map中取值
        Gson gson = new Gson();
        //Gson的fromJson方法，传参json格式的字符串，和要转成类型的class对象，key是String、value是Object
        HashMap map = gson.fromJson(result, HashMap.class);
        String accessToken = (String)map.get("access_token");
        String openid = (String)map.get("openid");

        //查询数据库当前用用户是否曾经使用过微信登录，数据库中有会存储openid字段，没有才去查询对应的用户信息，有对应的openid直接提示登录
        EduUser eduUser=eduUserService.getByOpenid(openid);
        if(eduUser==null){
            //访问微信的资源服务器，获取用户信息,
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpClientUtil.get(userInfoUrl);
                System.out.println("resultUserInfo==========" + resultUserInfo);
            } catch (Exception e) {
                throw new CustomException(20001, "拉取用户微信信息失败");
            }
            //解析json
            HashMap<String, Object> mapUserInfo = gson.fromJson(resultUserInfo, HashMap.class);
            String nickname = (String)mapUserInfo.get("nickname");//微信昵称
            String headimgurl = (String)mapUserInfo.get("headimgurl");//微信头像
            //向数据库中插入一条记录
            eduUser = new EduUser();
            eduUser.setNickname(nickname);
            eduUser.setOpenid(openid);//这个早就有了，做用户已经注册校验的
            eduUser.setAvatar(headimgurl);
            eduUserService.save(eduUser);
        }
        //如果已经注册过直接跳转登录页面即可，不需要提示信息
        //TODO 用户的手机号码是否需要绑定，不绑定完全可以再创建一个新的号码


        //登录，这儿需要完善用户已经登录的效果，而不是未登录前的页面，解决办法是在路径中传递token给前端
        //首页面created方法初始化的时候会尝试从cookie中获取数据，可以在服务端生成token传递给cookie，但是cookie无法做到跨域传递，域名不同传递不了，这种方式在微服务中是不行的
        // 生成token的数据只有用户id和昵称，一定注意token不带密码,插入数据id会回显
        String jwtToken = JwtUtils.getJwtToken(eduUser.getId(), eduUser.getNickname());

        return "redirect:http://localhost:3000?user_token="+jwtToken;
    }

    /**
     * @param session
     * @return {@link String }
     * @描述 获取微信登录二维码
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/07
     * @since 1.0.0
     */
    @GetMapping("login")
    public String genQrConnect(HttpSession session) {
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //获取业务服务器重定向地址
        String redirectUrl = ConstantProperties.WX_OPEN_REDIRECT_URL;
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(20001, e.getMessage());
        }
        // 防止csrf攻击（跨站请求伪造攻击）
        //String state = UUID.randomUUID().toString().replaceAll("-", "");//一般情况下会使用一个随机数
        String state = "imhelen";//为了让大家能够使用我搭建的外网的微信回调跳转服务器，这里填写你在ngrok的前置域名
        //System.out.println("state = " + state);
        // 采用redis等进行缓存state 使用sessionId为key 30分钟后过期，可配置
        //键： "wechar-open-state-" + httpServletRequest.getSession().getId()
        //值： satte
        //过期时间： 30分钟
        //生成qrcodeUrl

        String qrcodeUrl = String.format(
                baseUrl,
                ConstantProperties.WX_OPEN_APP_ID,
                redirectUrl,
                state);
        //把state信息放入redis做防攻击手段,设置五分钟时效性，但是这个不是会被地址栏看见吗？怎么防呢？
        redisTemplate.opsForValue().set("state",state,5, TimeUnit.MINUTES);
        return "redirect:" + qrcodeUrl;
        //这个扫描是跳转到尚硅谷的服务器，然后转发回来的地址，方便测试用，只需要把本地的地址改成返回的连接形式就能端口和路径与尚硅谷服务器返回结果一致就能回到本地接口
        //扫描后跳转的地址栏：http://localhost:8160/api/ucenter/wx/callback?code=071mkwFa1x2W9G0gz0Ia1va8Y02mkwF4&state=imhelen
    }

}
