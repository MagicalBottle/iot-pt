package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.LimiterService;
import com.service.PTService;
import io.netty.channel.Channel;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class PTServiceImpl implements PTService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    @Value("${pt.server.path}")
    private String parentPath;

    //消息限流器
    @Autowired
    private LimiterService limiterService;

    //消息处理线程池
    //消息数约1秒3000条
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(30, 50,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(600));


    /**
    *   @desc : 注册netty服务到zk
    *   @auth : TYF
    *   @date : 2020/3/15 - 15:21
    */
    @Override
    public void registryToZk(String host,int port) throws Exception{
        String addr = host+":"+port;
        String childNode = parentPath+"/"+addr;
        zkClient.create()
                //根节点
                .creatingParentsIfNeeded()
                //子节点为临时节点,zk会话断开自动清除
                .withMode(CreateMode.EPHEMERAL)
                //节点
                .forPath(childNode,"0".getBytes());
        logger.info("当前在线节点:"+zkClient.getChildren().forPath(parentPath));
    }


    /**
    *   @desc : 当前netty节点,客户端数量上报
    *   @auth : TYF
    *   @date : 2020-03-17 - 13:09
    */
    @Override
    public void clientCountReport(String host,int port,int count) throws Exception {
        String addr = host+":"+port;
        String childNode = parentPath+"/"+addr;
        zkClient.setData().forPath(childNode,String.valueOf(count).getBytes());
        logger.info("上报当前节点客户端数量" +addr+",count="+count);
    }


    /**
     *   @desc : 消息预处理
     *   @auth : TYF
     *   @date : 2020-03-16 - 15:24
     */
    @Override
    public void msgExecute(Channel channel, String msg) {
        logger.info("上行消息 "+msg);
        //空消息
        if(msg==null||"".equals(msg)){
            logger.info("客户端消息为空,踢掉连接");
            channel.close();
            return;
        }
        //消息协全部为json串
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
            channel.close();
            return;
        }
        if(!limiterService.tryChannelAcquire(channel)){
            logger.info("触发单个channel消息限流");
            channel.close();
            return;
        }


        //公共必传字段
        String serviceName = jObj.getString("service_name");//命令
        String clientId = jObj.getString("client_id");//客户端编号
        Integer msgType = jObj.getInteger("msgType");//消息类型


        //业务处理,上行消息包含两种 1.异步消息(推入mq异步处理)  2.同步消息(推入redis同步轮询)

    }


}
