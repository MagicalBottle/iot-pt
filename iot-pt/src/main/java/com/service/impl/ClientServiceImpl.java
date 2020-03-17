package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
*   @desc : 客户端逻辑处理
*   @auth : TYF
*   @date : 2020-03-16 - 14:45
*/
@Service
public class ClientServiceImpl implements ClientService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //通道缓存
    private static Map<Channel,String> channelMap = new ConcurrentHashMap<>();

    public static Map<Channel, String> getChannelMap() {
        return channelMap;
    }

    /**
    *   @desc : 缓存客户端通道
    *   @auth : TYF
    *   @date : 2020-03-16 - 14:54
    */
    @Override
    public void saveClient() {

    }

    /**
    *   @desc : 加载客户端通道
    *   @auth : TYF
    *   @date : 2020-03-16 - 14:55
    */
    @Override
    public void removeClient() {

    }

    /**
     *   @desc : 清除客户端通道
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:55
     */
    @Override
    public Channel loadClient() {
        return null;
    }


    /**
    *   @desc : 返回异常信息
    *   @auth : TYF
    *   @date : 2020-03-17 - 17:20
    */
    @Override
    public void clientError(Channel channel, String msg,String data) {
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
    public void clientLogin(Channel channel, String msg) {

    }

    /**
    *   @desc : 处理客户端心跳
    *   @auth : TYF
    *   @date : 2020-03-17 - 16:33
    */
    @Override
    public void clientHeart(Channel channel, String msg) {

    }

    /**
    *   @desc : 心跳和登陆以外的上行消息转发给业务处理程序
    *   @auth : TYF
    *   @date : 2020-03-17 - 16:36
    */
    @Override
    public void clientMsgReSend(Channel channel, String msg) {

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
