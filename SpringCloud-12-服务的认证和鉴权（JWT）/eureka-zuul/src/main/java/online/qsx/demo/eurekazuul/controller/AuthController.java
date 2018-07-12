/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.controller;

import online.qsx.demo.eurekazuul.jwt.JwtAuthenticationResponse;
import online.qsx.demo.eurekazuul.service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AuthController
 * @Description TODO
 * @Date 2018/07/11 15:48
 * @Author yuan yi xiong
 * @Version 1.0
 **/
@RestController
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthServiceImpl authServiceImpl;

    @Value("${jwt.header}")
    private String tokenHeader;

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(String username, String password) throws AuthenticationException {
        final String token = authServiceImpl.login(username, password);
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }
}
