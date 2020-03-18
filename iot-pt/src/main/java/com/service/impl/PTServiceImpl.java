package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.LimiterService;
import com.service.MsgService;
import com.service.PTService;
import com.utils.CommonMsgResult;
import com.utils.StringUtils;
import com.utils.redis.RedisDao;
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

    @Value("${pt.server.zk.path}")
    private String parentPath;

    @Value("${client.count.redis.prefix}")
    private String clientCountPrefix;

    //消息限流器
    @Autowired
    private LimiterService limiterService;

    //消息处理器
    @Autowired
    private MsgService msgService;

    @Autowired
    private RedisDao redisDao;

    //消息处理线程池
    //消息数约1秒3000条
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(150, 300,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(2000));


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
                .forPath(childNode,"online".getBytes());
        logger.info("当前在线节点:"+zkClient.getChildren().forPath(parentPath));
    }


    /**
    *   @desc : 当前netty节点,客户端数量上报redis
    *   @auth : TYF
    *   @date : 2020-03-17 - 13:09
    */
    @Override
    public void clientCountReport(String host,int port,int count,int exipre) throws Exception {
        String addr = host+":"+port;
        String key = clientCountPrefix+addr;
        redisDao.setString(key,String.valueOf(count),exipre+5);//过期时间比上报间隔时间长一点
        logger.info("上报节点客户端数量"+count+"个,本地"+addr);
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
            msgService.clientError(channel,"客户端消息为空",msg);
            return;
        }

        //全局消息限流
        if(!limiterService.tryGlobalAcquire()){
            logger.info("触发全局限流,请重试");
            msgService.clientError(channel,"触发全局限流,请重试",msg);
            return;
        }

        //心跳消息
        JSONObject jObj;
        if(msg.contains("0x11")){
            msgService.clientHeart(channel,msg);
            return;
        }
        //非心跳消息
        else{
            try {
                jObj = JSONObject.parseObject(msg);
            }
            catch (Exception e){
                logger.info("消息格式非标准json");
                msgService.clientError(channel,"消息格式非标准json",msg);
                return;
            }
        }

        //命令名称
        String serviceName = jObj.getString("service_name");
        //客户端编号
        String clientId = jObj.getString("client_id");
        if(!StringUtils.isNotNull(serviceName)||!StringUtils.isNotNull(clientId)){
            logger.info("消息缺少必传字段,serviceName="+serviceName+",clientId="+clientId);
            msgService.clientError(channel,"缺少必传字段",msg);
            return;
        }

        //登陆消息
        if("login".equals(serviceName)){
            msgService.clientLogin(channel,jObj);
            return;
        }
        //非登陆消息 且未登录
        else if(!msgService.clientIsLogin(channel)){
            logger.info("请先登陆!");
            msgService.clientError(channel,"请先登录!",msg);
            return;
        }
        //正常业务消息
        else{
            msgService.clientMsgReSend(channel,jObj);
            return;
        }


    }

}
