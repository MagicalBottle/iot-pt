package com.service;

import io.netty.channel.Channel;

public interface LimiterService {

    boolean tryGlobalAcquire();

    boolean tryChannelAcquire(String clientId);

    void deleteChannelLimiter(String clientId);

    void saveChannelLimiter(String clientId);

}
