package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.MsgService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgServiceImpl implements MsgService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientService clientService;


    /**
     *   @desc : 返回异常信息
     *   @auth : TYF
     *   @date : 2020-03-17 - 17:20
     */
    @Override
    public void clientError(Channel channel, String msg, String data) {
        JSONObject res = new JSONObject();
        res.put("state",0);
        res.put("msg",msg);
        res.put("data",data);
        msgResp(channel,res.toJSONString());
    }

    /**
     *   @desc : 处理客户端登陆
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:33
     */
    @Override
    public void clientLogin(Channel channel, JSONObject msg) {
        //客户端编号
        String clientId = msg.getString("client_id");
        //查询客户端
        //查询redis是否有登录信息排除重复登陆
        clientService.saveChannel(channel,clientId);
        //保存登录信息到redis
        //返回登录响应
    }

    /**
     *   @desc : 处理客户端心跳
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:33
     */
    @Override
    public void clientHeart(Channel channel, String heart) {
        //保存心跳信息到redis
        //返回心跳响应
    }

    /**
     *   @desc : 心跳和登陆以外的上行消息转发给业务处理程序
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:36
     */
    @Override
    public void clientMsgReSend(Channel channel, JSONObject msg) {
        //调用grpc传给业务服务器
    }

    /**
     *   @desc : 消息下行
     *   @auth : TYF
     *   @date : 2020-03-16 - 15:45
     */
    @Override
    public void msgResp(Channel channel, String msg) {
        //通道不可写
        if(channel==null||!channel.isActive()||!channel.isWritable()) {
            logger.info("通道不可写");
            return;
        }
        //消息下行
        if(msg!=null&&!"".equals(msg)){
            try {
                byte[] req = new StringBuffer().append(msg).append("\n").toString().getBytes("UTF-8");
                ByteBuf buf = Unpooled.buffer(req.length);
                buf.writeBytes(req);
                channel.writeAndFlush(buf);
            }catch (Exception e){
                logger.info("消息下发异常");
                return;
            }
        }
        return;
    }

}
