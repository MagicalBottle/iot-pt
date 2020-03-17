package com.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *   @desc : 客户端处理
 *   @auth : TYF
 *   @date : 2020/3/16 - 23:35
 */
@RestController
@RequestMapping("/client")
public class ClientController {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/create")
    public String  create(HttpServletRequest req, HttpServletResponse resp){
        return null;
    }

    @RequestMapping("/delete")
    public String  delete(HttpServletRequest req, HttpServletResponse resp){
        return null;
    }

    @RequestMapping("/update")
    public String  update(HttpServletRequest req, HttpServletResponse resp){
        return null;
    }


    @RequestMapping("/query")
    public String  query(HttpServletRequest req, HttpServletResponse resp){
        return null;
    }


}
