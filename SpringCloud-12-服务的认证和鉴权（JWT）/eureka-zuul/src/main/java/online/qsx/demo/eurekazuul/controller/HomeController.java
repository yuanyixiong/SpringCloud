/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ClassName HomeController
 * @Description TODO
 * @Date 2018/07/11 10:41
 * @Author yuan yi xiong
 * @Version 1.0
 **/
@RestController
public class HomeController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/")
    public ModelAndView index(ModelAndView mav) {
        logger.info("HomeController index");
        mav.addObject("msg", "测试标题");
        mav.setViewName("index");
        return mav;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @RequestMapping(value = "/admin/test1")
    @ResponseBody
    public String adminTest1() {
        logger.info("HomeController adminTest1-->ROLE_USER");
        return "ROLE_USER";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping("/admin/test2")
    @ResponseBody
    public String adminTest2() {
        logger.info("HomeController adminTest2-->ROLE_ADMIN");
        return "ROLE_ADMIN";
    }

}
