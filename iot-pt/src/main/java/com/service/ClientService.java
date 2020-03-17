package com.service;


import io.netty.channel.Channel;

public interface ClientService {

    String loadClientId(Channel channel);

    void saveChannel(Channel channel,String clientId);

    Channel loadChannel(String clientId);

    void deleteChannel(String clientId);

    void deleteChannel(Channel channel);




}
