package com.service.impl;


import com.google.common.util.concurrent.RateLimiter;
import com.service.LimiterService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
*   @desc : 用于客户端消息流量控制
*   @auth : TYF
*   @date : 2020-03-16 - 13:51
*/
@Service
public class LimiterServiceImpl implements LimiterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //全局每秒令牌数
    @Value("${global.limiter.ticket.count}")
    private Integer globalCount;

    //单独客户端每秒令牌数
    @Value("${channel.limiter.ticket.count}")
    private Integer channelCount;

    //全局限流器
    private static RateLimiter globalLimiter;

    //通道限流器
    private static Map<Channel,RateLimiter> channelLimiter = new ConcurrentHashMap<>();

    /**
    *   @desc : 全局客户端消息限流
    *   @auth : TYF
    *   @date : 2020-03-16 - 14:13
    */
    @Override
    public boolean tryGlobalAcquire() {
        if(globalLimiter==null){
            logger.info("全局客户端消息每秒限制"+globalCount+"条");
            globalLimiter = RateLimiter.create(globalCount);
        }
        return globalLimiter.tryAcquire();
    }

    /**
    *   @desc : 单独客户端消息限流
    *   @auth : TYF
    *   @date : 2020-03-16 - 14:13
    */
    @Override
    public boolean tryChannelAcquire(Channel channel) {
        //新客户端需要先创建限流器
        if(channelLimiter.get(channel)==null){
            logger.info("单独客户端消息每秒限制"+channelCount+"条");
            channelLimiter.put(channel,RateLimiter.create(channelCount));
        }
        return channelLimiter.get(channel).tryAcquire();
    }
}
