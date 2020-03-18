package com.service.impl;


import com.google.common.util.concurrent.RateLimiter;
import com.service.LimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private static Map<String,RateLimiter> channelLimiter = new ConcurrentHashMap<>();

    @PostConstruct
    private void initLimiter(){
        globalLimiter = RateLimiter.create(globalCount);
        logger.info("全局客户端消息每秒限制"+globalCount+"条");
    }


    /**
    *   @desc : 清除channel限流器
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:49
    */
    @Override
    public void deleteChannelLimiter(String clientId) {
        if(clientId!=null){
            channelLimiter.remove(clientId);
        }
    }

    /**
    *   @desc : 创建限流器
    *   @auth : TYF
    *   @date : 2020-03-18 - 15:04
    */
    @Override
    public void saveChannelLimiter(String clientId) {
        logger.info("单个客户端消息每秒限制"+channelCount+"条");
        channelLimiter.put(clientId,RateLimiter.create(channelCount));
    }

    /**
     *   @desc : 全局客户端消息限流(心跳和登陆除外)
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    @Override
    public boolean tryGlobalAcquire() {
        return globalLimiter.tryAcquire();
    }

    /**
     *   @desc : 单独客户端消息限流(心跳和登陆除外)
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    @Override
    public boolean tryChannelAcquire(String clientId) {
        return channelLimiter.get(clientId).tryAcquire();
    }


}
