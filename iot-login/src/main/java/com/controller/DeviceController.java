package com.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *   @desc : 设备信息
 *   @auth : TYF
 *   @date : 2020/3/16 - 23:35
 */
@Controller
@RequestMapping("/device")
public class DeviceController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *   @desc : 获取节点设备信息
     *   @auth : TYF
     *   @date : 2020/3/16 - 23:43
     */
    @RequestMapping("/info")
    public String  login(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        return "info.html";
    }



}
