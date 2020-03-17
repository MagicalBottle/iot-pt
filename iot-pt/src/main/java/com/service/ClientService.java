package com.service;

import io.netty.channel.Channel;

public interface ClientService {


    void saveClient();

    void removeClient();

    Channel loadClient();

    void msgResp(Channel channel,String msg);



}
