package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.utils.RequestUtil;
import com.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *   @desc : 处理用户登录
 *   @auth : TYF
 *   @date : 2020/3/16 - 23:35
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *   @desc : 获取token,服务器地址
     *   @auth : TYF
     *   @date : 2020/3/16 - 23:43
     */
    @RequestMapping("/login")
    public String  login(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //用户名
        String userId = RequestUtil.getString(req,"userId",null);
        //密码
        String password = RequestUtil.getString(req,"password",null);

        logger.info("用户登陆 "+userId+","+password);

        //响应
        JSONObject res = new JSONObject();
        res.put("msg","登陆成功");
        res.put("state",1);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;
    }



}
