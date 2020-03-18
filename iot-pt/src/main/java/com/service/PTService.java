package com.service;

import io.netty.channel.Channel;

public interface PTService {

    void registryToZk(String host,int port) throws Exception;

    void clientCountReport(String host,int port,int count,int exipre) throws Exception;

    void msgPreExecute(Channel channel, String msg);

}
