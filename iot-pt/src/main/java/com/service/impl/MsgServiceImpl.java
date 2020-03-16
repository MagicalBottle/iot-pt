package com.service.impl;

import com.service.MsgService;
import io.netty.channel.Channel;
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

    //消息处理线程池
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(10, 10,0L, TimeUnit.SECONDS,new ArrayBlockingQueue(10));


    /**
    *   @desc : 上行消息解析
    *   @auth : TYF
    *   @date : 2020-03-16 - 15:24
    */
    @Override
    public void msgHandler(Channel channel, String msg) {






    }
}
