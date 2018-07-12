/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.service;

import online.qsx.demo.eurekazuul.dto.SysUser;
import online.qsx.demo.eurekazuul.jwt.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @ClassName AuthServiceImpl
 * @Description TODO
 * @Date 2018/07/11 15:41
 * @Author yuan yi xiong
 * @Version 1.0
 **/
@Service
public class AuthServiceImpl {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 登陆，授予Token
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        logger.info("登陆，授予Token");
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        // Perform the security
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Reload password post-security so we can generate token
        final SysUser sysUser = sysUserService.loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(sysUser);
        return token;
    }

}
