package com.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;

public interface MsgService {

    void clientError(Channel channel, String msg, String data);

    void clientLogin(Channel channel, JSONObject msg);

    boolean clientIsLogin(Channel channel);

    void clientHeart(Channel channel, String msg);

    void clientMsgReSend(Channel channel,JSONObject msg);

    void msgResp(Channel channel,String msg);

}
