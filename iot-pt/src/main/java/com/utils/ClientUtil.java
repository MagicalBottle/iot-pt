package com.utils;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
*   @desc : 客户端工具类
*   @auth : TYF
*   @date : 2020-03-19 - 16:18
*/
@Service
public class ClientUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    /**
     *   @desc : 通过channel查找clientId
     *   @auth : TYF
     *   @date : 2020/3/17 - 19:39
     */
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
     *   @desc : 清除通道缓存
     *   @auth : TYF
     *   @date : 2020/3/17 - 20:14
     */
    public void deleteChannel(String clientId) {
        if(clientId!=null){
            channelMap.remove(clientId);
        }
    }


}
