package com.service.impl;


import com.service.ClientService;
import com.utils.IPUtil;
import com.utils.redis.RedisDao;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RedisDao redisDao;

    //redis登陆信息缓存前缀
    @Value("${client.login.redis.prefix}")
    private String loginPrefix;

    @Value("${netty.port}")
    private int port;

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
        if(clientId==null){
            logger.info("clientId为空保存channel失败");
            return;
        }
        //clientId放到channel内置属性中,方便channel查找clientId
        AttributeKey<String> key = AttributeKey.valueOf("clientId");
        Attribute<String> attr = channel.attr(key);
        attr.set(clientId);
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
        if(clientId!=null){
            channelMap.remove(clientId);
        }else{
            logger.info("清除通道缓存失败 clientId为null");
        }
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


    /**
     *   @desc : 缓存登陆信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    @Override
    public void saveLoginInfo(Channel channel) {
        String clientId = loadClientId(channel);
        String key = loginPrefix+clientId;
        String value = IPUtil.getLocalHostIp()+":"+port;
        redisDao.setString(key,value);
    }

    /**
     *   @desc : 清除登录信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    @Override
    public void deleteLoginInfo(Channel channel) {
        String clientId = loadClientId(channel);
        String key = loginPrefix+clientId;
        redisDao.delString(key);
    }

    /**
    *   @desc : 缓存心跳信息
    *   @auth : TYF
    *   @date : 2020-03-18 - 13:35
    */
    @Override
    public void saveHeartInfo(Channel channel) {
        String clientId = loadClientId(channel);
        redisDao.setString(clientId,String.valueOf(System.currentTimeMillis()));//缓存心跳时间
    }
}
