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
 * @ClassName Massage
 * @Description TODO
 * @Date 2018/07/11 14:50
 * @Author yuan yi xiong
 * @Version 1.0
 **/
public class Massage {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String title;

    private String content;

    private String extraInfo;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Massage(String title, String content, String extraInfo) {
        this.title = title;
        this.content = content;
        this.extraInfo = extraInfo;
    }

    public Massage() {
    }
}
