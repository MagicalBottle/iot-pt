package com.service;

import io.netty.channel.Channel;

public interface LimiterService {

    boolean tryGlobalAcquire();

    boolean tryChannelAcquire(Channel channel);

}
