package com.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.service.RouterService;
import com.utils.RequestUtil;
import com.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*   @desc : 处理设备鉴权和路由
*   @auth : TYF
*   @date : 2020/3/16 - 23:35
*/
@RestController
@RequestMapping("/common")
public class RouterController {


    @Autowired
    private RouterService routerService;


    /**
    *   @desc : 获取token,服务器地址
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:43
    */
    @RequestMapping("/preLogin")
    public String  preLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //客户端编号
        Long client_id = RequestUtil.getLong(req,"client_id",-1L);

        //服务器host
        String host = routerService.getOneOnlinePT();

        //响应
        JSONObject res = new JSONObject();
        res.put("host",host);
        res.put("state",1);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;

    }


    /**
     *   @desc : 获取pt-server节点状态信息
     *   @auth : TYF
     *   @date : 2020/3/16 - 23:43
     */
    @RequestMapping("/getPtStatus")
    public String  getPtStatus(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //服务器host
        JSONObject data = routerService.getPtStatus();
        //响应
        JSONObject res = new JSONObject();
        res.put("state",1);
        res.put("data",data);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;

    }


    /**
     *   @desc : 获取客户端心跳记录
     *   @auth : TYF
     *   @date : 2020/3/16 - 23:43
     */
    @RequestMapping("/getClientHeart")
    public String  getClientHeart(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //服务器host
        JSONObject data = routerService.getClientHeart();
        //响应
        JSONObject res = new JSONObject();
        res.put("state",1);
        res.put("data",data);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;

    }


    /**
     *   @desc : 获取客户端登陆记录
     *   @auth : TYF
     *   @date : 2020/3/16 - 23:43
     */
    @RequestMapping("/getClientConn")
    public String  getClientConn(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //服务器host
        JSONObject data = routerService.getClientConn();
        //响应
        JSONObject res = new JSONObject();
        res.put("state",1);
        res.put("data",data);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;

    }



}
