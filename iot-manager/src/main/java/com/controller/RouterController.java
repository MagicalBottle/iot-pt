package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.entry.ClientTb;
import com.service.ClientService;
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

    @Autowired
    private ClientService clientService;

    /**
    *   @desc : 获取token,服务器地址
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:43
    */
    @RequestMapping("/preLogin")
    public String  preLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //客户端编号
        Long client_id = RequestUtil.getLong(req,"client_id",-1L);
        ClientTb clientTb = clientService.queryById(client_id);
        if(client_id==null||"".equals(client_id)||clientTb==null){
            //响应
            JSONObject res = new JSONObject();
            res.put("state",0);
            res.put("msg","该设备状态异常!");
            ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
            return null;
        }


        //登录token
        String token = routerService.getCachedToken(client_id);
        //服务器host
        String host = routerService.getOneOnlinePT();

        //响应
        JSONObject res = new JSONObject();
        res.put("token",token);
        res.put("host",host);
        res.put("state",1);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;

    }


}
