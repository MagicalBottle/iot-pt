package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.LimiterService;
import com.service.MsgService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
*   @desc : 消息处理器
*   @auth : TYF
*   @date : 2020-03-16 - 14:49
*/
@Service
public class MsgServiceImpl implements MsgService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //消息限流器
    @Autowired
    private LimiterService limiterService;

    //消息处理线程池
    //消息数约1秒3000条
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(30, 50,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(600));

    /**
    *   @desc : 消息预处理
    *   @auth : TYF
    *   @date : 2020-03-16 - 15:24
    */
    @Override
    public void msgExecute(Channel channel, String msg) {
        //空消息
        if(msg==null||"".equals(msg)){
            logger.info("客户端消息为空,踢掉连接");
            channel.close();
            return;
        }
        //消息协全部为json串(包括心跳)
        JSONObject jObj;
        try {
            jObj = JSONObject.parseObject(msg);
        }
        catch (Exception e){
            logger.info("客户端消息非json串");
            channel.close();
            return;
        }
        //消息限流
        if(!limiterService.tryGlobalAcquire()){
            logger.info("触发全局消息限流");
            //返回公共失败响应
            return;
        }
        if(!limiterService.tryChannelAcquire(channel)){
            logger.info("触发单个channel消息限流");
            //返回公共失败响应
            return;
        }


        //消息类型
        String serviceName = jObj.getString("service_name");

        //业务处理,上行消息包含两种 1.客户端主动上行(推入mq异步处理)  2.客户端响应上行(推入redis同步轮询)

    }

}
