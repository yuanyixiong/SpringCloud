/********************************************
 * Copyright (c) , yuan yi xiong
 *
 * All rights reserved
 *
 *********************************************/
package online.qsx.demo.eurekazuul.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName SysRole
 * @Description TODO
 * @Date 2018/07/11 10:33
 * @Author yuan yi xiong
 * @Version 1.0
 **/
public class SysRole {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long id;

    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SysRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public SysRole() {
    }
}
