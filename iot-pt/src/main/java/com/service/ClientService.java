package com.service;

import io.netty.channel.Channel;

public interface ClientService {


    void saveClient();

    void removeClient();

    Channel loadClient();

    void clientError(Channel channel, String msg,String data);

    void clientLogin(Channel channel, String msg);

    void clientHeart(Channel channel, String msg);

    void clientMsgReSend(Channel channel,String msg);

    void msgResp(Channel channel,String msg);



}
