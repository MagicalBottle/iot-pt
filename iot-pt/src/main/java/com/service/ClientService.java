package com.service;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;

public interface ClientService {

    //通道缓存相关
    void saveChannel(Channel channel,String clientId);
    String loadClientId(Channel channel);
    void deleteChannel(String clientId);
    void saveLoginInfo(String clientId);
    void deleteLoginInfo(String clientId);
    void saveHeartInfo(String clientId);

    //限流器相关
    boolean tryGlobalAcquire();
    void deleteChannelLimiter(String clientId);
    void saveChannelLimiter(String clientId);
    boolean tryChannelAcquire(String clientId);

    //缓存管理
    void saveCache(Channel channel,String clientId);
    void removeCache(String clientId);


    //消息处理
    void msgPreExecute(Channel channel, String msg);
    void clientError(Channel channel, String msg, String data);
    void clientSuccess(Channel channel, String msg, String data);
    void clientLogin(Channel channel, JSONObject msg);
    boolean clientIsLogin(Channel channel);
    void clientHeart(Channel channel, String msg);
    void clientMsgReSend(Channel channel,JSONObject msg);

    //消息下行
    void msgResp(Channel channel,String msg);

}
