package com.atlisheng.security.config;

import com.atlisheng.security.filter.TokenAuthenticationFilter;
import com.atlisheng.security.filter.TokenLoginFilter;
import com.atlisheng.security.security.DefaultPasswordEncoder;
import com.atlisheng.security.security.TokenLogoutHandler;
import com.atlisheng.security.security.TokenManager;
import com.atlisheng.security.security.UnauthorizedEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 * Security核心配置类
 * Spring Security的核心配置就是继承WebSecurityConfigurerAdapter并注解@EnableWebSecurity的配
 * 置。这个配置指明了用户名密码的处理方式、请求路径的开合、登录登出控制等和安全相关的配置
 *  类名可以随意取，但是必须继承WebSecurityConfigurerAdapter
 * </p>
 *
 * @author qy
 * @since 2019-11-18
 */
@Configuration
//以下两个注解是开启SpringSecurity权限控制的注解
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    //userDetailsService:用户登录时需要查询数据库，UserDetailsService是SpringSecurity提供的接口，
    // 实现类似乎要在服务中自己写
    private UserDetailsService userDetailsService;
    //生成Token的工具类，这个类需要自己写
    private TokenManager tokenManager;
    //处理密码的工具类
    private DefaultPasswordEncoder defaultPasswordEncoder;
    //redis,这个是在Common中引入的
    private RedisTemplate redisTemplate;

    @Autowired//我靠，还有这种用法
    public TokenWebSecurityConfig(UserDetailsService userDetailsService, DefaultPasswordEncoder defaultPasswordEncoder,
                                  TokenManager tokenManager, RedisTemplate redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 核心配置设置
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(new UnauthorizedEntryPoint())
                .and().csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                //设置退出请求的地址，退出的操作是SpringSecurity做到的
                .and().logout().logoutUrl("/admin/acl/index/logout")
                .addLogoutHandler(new TokenLogoutHandler(tokenManager,redisTemplate)).and()
                .addFilter(new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate))
                .addFilter(new TokenAuthenticationFilter(authenticationManager(), tokenManager, redisTemplate)).httpBasic();
    }

    /**
     * 密码处理
     * @param auth
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        //在defaultPasswordEncoder中有对密码进行MD5加密的方法，这里面使用的是MD5工具类中的方法，只是简单的方法调用，
        //并没有重写MD5加密的方法
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     * 配置哪些请求不拦截，即哪些请求不需要访问权限控制，比如swagger，可以直接访问
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**"
               );
        //web.ignoring().antMatchers("/*/**");
    }
}