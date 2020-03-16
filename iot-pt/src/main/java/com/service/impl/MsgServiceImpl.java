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

    //客户端处理器
    @Autowired
    private ClientService clientService;

    //消息处理线程池
    //消息数约1秒3000条
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(30, 50,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(600));


    /**
    *   @desc : 上行消息解析
    *   @auth : TYF
    *   @date : 2020-03-16 - 15:24
    */
    @Override
    public void msgHandler(Channel channel, String msg) {
        //空消息
        if(msg==null||"".equals(msg)){
            logger.info("客户端消息为空,踢掉连接");
            channel.close();
            return;
        }
        //心跳消息
        else if("0x11".equals(msg)){
            //未登录直接
            clientService.msgResp(channel,"0x12");
            return;
        }
        //协议解析
        JSONObject jObj;
        try {
            jObj = JSONObject.parseObject(msg);
        }
        catch (Exception e){
            logger.info("客户端消息非json串");
            channel.close();
            return;
        }
        //消息类型
        String service_name = jObj.getString("service_name");
        //设备id
        String device_id = jObj.getString("device_id");

        logger.info("命令:"+service_name+",设备号:"+device_id);

    }
}
