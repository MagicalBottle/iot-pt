package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.entry.ClientTb;
import com.service.ClientService;
import com.utils.RequestUtil;
import com.utils.ResponseUtil;
import com.utils.SnowFlakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private ClientService clientService;

    @RequestMapping("/create")
    public String  create(HttpServletRequest req, HttpServletResponse resp){

        //设备名称
        String name = RequestUtil.getString(req,"name",null);
        //备注
        String remark = RequestUtil.getString(req,"remark",null);
        //设备类型
        Integer type = RequestUtil.getInteger(req,"type",-1);
        //待添加客户端
        ClientTb c = new ClientTb();
        c.setId(SnowFlakeIdWorker.getInstance().nextId());
        c.setName(name);
        c.setRemark(remark);
        c.setType(type);
        int count = clientService.create(c);
        //响应
        JSONObject res = new JSONObject();
        res.put("state",count);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;
    }

    @RequestMapping("/delete")
    public String  delete(HttpServletRequest req, HttpServletResponse resp){

        //设备编号
        Long id = RequestUtil.getLong(req,"id",-1L);
        //待删除记录
        ClientTb c = new ClientTb();
        c.setId(id);
        int count = clientService.delete(c);
        //响应
        JSONObject res = new JSONObject();
        res.put("state",count);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;
    }

    @RequestMapping("/update")
    public String  update(HttpServletRequest req, HttpServletResponse resp){
        //设备名称
        String name = RequestUtil.getString(req,"name",null);
        //备注
        String remark = RequestUtil.getString(req,"remark",null);
        //设备类型
        Integer type = RequestUtil.getInteger(req,"type",-1);
        //待添加客户端
        ClientTb c = new ClientTb();
        c.setId(SnowFlakeIdWorker.getInstance().nextId());
        c.setName(name);
        c.setRemark(remark);
        c.setType(type);
        int count = clientService.update(c);
        //响应
        JSONObject res = new JSONObject();
        res.put("state",count);
        ResponseUtil.ajaxOutputSTR(resp,res.toJSONString());
        return null;
    }


    @RequestMapping("/query")
    public String  query(HttpServletRequest req, HttpServletResponse resp){
        return null;
    }


}
