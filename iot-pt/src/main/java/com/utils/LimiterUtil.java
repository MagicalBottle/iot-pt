package com.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
*   @desc : 限流器配置
*   @auth : TYF
*   @date : 2020-03-19 - 16:23
*/
@Service
public class LimiterUtil {

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

    /**
     *   @desc : 静态参数初始化
     *   @auth : TYF
     *   @date : 2020-03-19 - 16:15
     */
    @PostConstruct
    private void init(){
        globalLimiter = RateLimiter.create(globalCount);
        logger.info("全局客户端消息每秒限制"+globalCount+"条");
    }


    /**
     *   @desc : 清除channel限流器
     *   @auth : TYF
     *   @date : 2020/3/17 - 20:49
     */
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
    public void saveChannelLimiter(String clientId) {
        logger.info("单个客户端消息每秒限制"+channelCount+"条");
        channelLimiter.put(clientId, RateLimiter.create(channelCount));
    }

    /**
     *   @desc : 全局客户端消息限流
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    public boolean tryGlobalAcquire() {
        return globalLimiter.tryAcquire();
    }

    /**
     *   @desc : 单独客户端消息限流
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    public boolean tryChannelAcquire(String clientId) {
        return channelLimiter.get(clientId).tryAcquire();
    }


}
