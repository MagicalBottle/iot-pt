package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.LimiterService;
import com.service.PTService;
import com.utils.CommonMsgResult;
import com.utils.StringUtils;
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

    //客户端处理器
    @Autowired
    private ClientService clientService;

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
     *   @desc : 消息预处理(心跳和登陆)
     *   @auth : TYF
     *   @date : 2020-03-16 - 15:24
     */
    @Override
    public void msgPreExecute(Channel channel, String msg) {

        //空消息
        if(msg==null||"".equals(msg)){
            logger.info("客户端消息为空");
            clientService.clientError(channel,"客户端消息为空",msg);
            return;
        }
        //消息协全部为json串
        JSONObject jObj;
        try {
            jObj = JSONObject.parseObject(msg);
        }
        catch (Exception e){
            logger.info("消息格式非标准json");
            clientService.clientError(channel,"消息格式非标准json",msg);
            return;
        }
        //消息限流
        if(!limiterService.tryGlobalAcquire()){
            logger.info("触发全局限流,请重试");
            clientService.clientError(channel,"触发全局限流,请重试",msg);
            return;
        }
        if(!limiterService.tryChannelAcquire(channel)){
            logger.info("触发客户端消息限流,请重试");
            clientService.clientError(channel,"触发客户端消息限流,请重试",msg);
            return;
        }

        //公共必传字段
        String serviceName = jObj.getString("service_name");//命令
        String clientId = jObj.getString("client_id");//客户端编号
        String actionId = jObj.getString("action_id");//交互ID,用于双方匹配上下行

        //消息异常
        if(!StringUtils.isNotNull(serviceName)||!StringUtils.isNotNull(clientId)||!StringUtils.isNotNull(actionId)){
            logger.info("消息异常缺少必传字段,serviceName="+serviceName+",clientId="+clientId+",actionId="+actionId);
            clientService.clientError(channel,"缺少必传字段",msg);
            return;
        }

        //登陆
        if("login".equals(serviceName)){
            clientService.clientLogin(channel,msg);
            return;
        }

        //心跳
        else if(serviceName.contains("0x11")){
            clientService.clientHeart(channel,msg);
            return;
        }
        //其他消息转发给业务程序
        else{
            clientService.clientMsgReSend(channel,msg);
            return;
        }


    }


}
