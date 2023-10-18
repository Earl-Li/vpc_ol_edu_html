package com.atlisheng.commonutils.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author Earl
 * @version 1.0.0
 * @描述 这里面的静态方法都会用在用户登录验证中
 * @创建日期 2023/10/04
 * @since 1.0.0
 */
public class JwtUtils {

    //定义两个常量
    public static final long EXPIRE = 1000 * 60 * 60 * 24;//EXPIRE是token过期时间，这里是1天
    public static final String APP_SECRET = "zAhmndMQBR3769PQISABsPy30XPHKG";//服务器存储做签名哈希的密码

    /**
     * @param id 用户id
     * @param nickname 用户昵称
     * @return {@link String }
     * @描述 生成token字符串的方案，传入用户id和昵称生成JWT令牌，还可以传入其他的信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    public static String getJwtToken(String id, String nickname){
        //生成JWT令牌
        String JwtToken = Jwts.builder()
                //设置JWT头信息
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")

                //设置分类，名字随便起
                .setSubject("vpc-ol-user")
                //设置当前时间
                .setIssuedAt(new Date())
                //设置过期时间为当前时间加上期望值
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                //设置token的主体部分，用户信息，多个可以加多行
                .claim("id", id)
                .claim("nickname", nickname)

                //签名哈希，加密方式和密钥
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        return JwtToken;
    }

    /**
     * @param jwtToken
     * @return boolean
     * @描述 判断token是否存在与有效，伪造的也会返回false，这个是直接传入token字符串
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;//如果token为空直接返回false
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);//使用密钥判断token是否是有效的，有异常就不是有效的
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;//没有异常是有效的就返回true
    }

    /**
     * @param request
     * @return boolean
     * @描述 判断token是否存在与有效，这个和上面的方法的区别是传递的是请求参数
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");//从请求中获取头信息token
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param request
     * @return {@link String }
     * @描述 根据request对象得到token，通过token获取用户id以及其他信息
     * @author Earl
     * @version 1.0.0
     * @创建日期 2023/10/04
     * @since 1.0.0
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        System.out.println(headerNames);
        String jwtToken = request.getHeader("token");
        System.out.println(jwtToken);
        if(StringUtils.isEmpty(jwtToken)) return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);//解析token
        Claims claims = claimsJws.getBody();//获取主体部分
        return (String)claims.get("id");//获取用户id
    }
}
