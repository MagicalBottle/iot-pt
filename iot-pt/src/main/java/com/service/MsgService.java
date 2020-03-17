package com.service;

import io.netty.channel.Channel;

public interface MsgService {

    void msgExecute(Channel channel,String msg);

}
