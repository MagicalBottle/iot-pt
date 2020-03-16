package com.service;

import io.netty.channel.Channel;

public interface MsgService {

    void msgHandler(Channel channel,String msg);

}
