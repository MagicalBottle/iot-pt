package com.service;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;

public interface ClientService {


    //缓存管理
    void saveCache(Channel channel,String clientId);
    void removeCache(String clientId);

    //消息处理
    void msgPreExecute(Channel channel, String msg);
    void clientLogin(Channel channel, JSONObject msg);
    boolean clientIsLogin(Channel channel);
    void clientHeart(Channel channel, String msg);
    void clientMsgReSend(Channel channel,JSONObject msg);
    //消息下行
    void msgResp(Channel channel,String msg);

}
