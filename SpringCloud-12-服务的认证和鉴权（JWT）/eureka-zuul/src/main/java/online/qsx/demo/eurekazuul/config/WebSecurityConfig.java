/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.config;

import online.qsx.demo.eurekazuul.jwt.JwtAuthenticationTokenFilter;
import online.qsx.demo.eurekazuul.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

/**
 * @ClassName WebSecurityConfig
 * @Description TODO
 * @Date 2018/07/11 13:56
 * @Author yuan yi xiong
 * @Version 1.0
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysUserService sysUserService;

    /**
     * 密码授权
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //指定用户登陆的Service
        auth.userDetailsService(sysUserService)
                //密码加密/解密
                .passwordEncoder(new PasswordEncoder() {
                    @Override
                    public String encode(CharSequence rawPassword) {
                        return rawPassword.toString();
                    }

                    @Override
                    public boolean matches(CharSequence charSequence, String encodedPassword) {
                        return Objects.equals(charSequence, encodedPassword);
                    }
                });
    }

    /**
     * 权限配置
     *
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        logger.info("开始权限配置");
        httpSecurity
                .csrf().disable()// 由于使用的是JWT，这里不需要csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()// 基于token，所以不需要session
                .authorizeRequests()//[访问URL][开始配置]
                .antMatchers("/auth/**").permitAll()// 允许匿名访问
                .anyRequest().authenticated() //尚未匹配的URL要求用户进行身份验证
                .and()//[访问URL][结束配置]
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())//[访问未经授权处理]
                .and()
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)// 添加JWT filter
                .headers().cacheControl();// 禁用缓存
        logger.info("完成权限配置");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 访问未经授权处理
     *
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, accessDeniedException) -> {
            logger.info("访问未经授权处理");
            String header = httpServletRequest.getHeader("X-Requested-With");
            boolean isAjax = header != null && "XMLHttpRequest".equals(header);
            //是否是ajax请求
            if (!isAjax) {
                logger.info("同步请求权限不足");
                httpServletResponse.sendRedirect("/403");
            } else {
                logger.info("ajax请求权限不足");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.setContentType("text/plain");
                httpServletResponse.getWriter().write("权限不足");
                httpServletResponse.getWriter().close();
            }
        };
    }

    /**
     * 登陆认证成功处理,完成加载用户菜单
     *
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            logger.info("登入认证成功");
            //检查当前用户是否拥有登入系统的权限
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                logger.info(grantedAuthority.getAuthority());
            }
            //当前登入用户,有登入系统的角色权限[去首页]
            httpServletRequest.getRequestDispatcher("/").forward(httpServletRequest, httpServletResponse);
        };
    }

    /**
     * 引入JWT
     *
     * @return
     * @throws Exception
     */
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

}
