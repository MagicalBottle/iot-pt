package com.service;


import io.netty.channel.Channel;

public interface ClientService {

    void saveChannel(Channel channel,String clientId);

    String loadClientId(Channel channel);

    Channel loadChannel(String clientId);

    void deleteChannel(String clientId);

    void deleteChannel(Channel channel);

    void saveLoginInfo(Channel channel);

    void deleteLoginInfo(Channel channel);

    void saveHeartInfo(Channel channel);





}
