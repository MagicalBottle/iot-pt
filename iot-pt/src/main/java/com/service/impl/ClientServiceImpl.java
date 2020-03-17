package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
*   @desc : 客户端channel管理
*   @auth : TYF
*   @date : 2020-03-16 - 14:45
*/
@Service
public class ClientServiceImpl implements ClientService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //通道缓存
    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    
    /**
    *   @desc : 通过channel查找clientId
    *   @auth : TYF
    *   @date : 2020/3/17 - 19:39
    */
    @Override
    public String loadClientId(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("clientId");
        Attribute<String> attr = channel.attr(key);
        String clientId= attr.get();
        return clientId;
    }


    /**
    *   @desc : 保存client
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:10
    */
    @Override
    public void saveChannel(Channel channel,String clientId) {
        //clientId放到channel内置属性中,方便channel查找clientId
        AttributeKey<byte[]> key = AttributeKey.valueOf("clientId");
        byte[] data = clientId.getBytes();
        Attribute<byte[]> attr = channel.attr(key);
        attr.set(data);
        //再将channel保存到map中
        channelMap.put(clientId,channel);
    }

    /**
    *   @desc : 通过clienId查找channel
    *   @auth : TYF
    *   @date : 2020/3/17 - 19:57
    */
    @Override
    public Channel loadChannel(String clientId) {
        return channelMap.get(clientId);
    }


    /**
    *   @desc : 清除通道缓存
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:14
    */
    @Override
    public void deleteChannel(String clientId) {
        channelMap.remove(clientId);
    }


    /**
    *   @desc : 清除通道缓存
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:15
    */
    @Override
    public void deleteChannel(Channel channel) {
        channelMap.remove(loadClientId(channel));
    }






}
