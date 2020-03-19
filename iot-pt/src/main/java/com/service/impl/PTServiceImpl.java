package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.PTService;
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

    @Autowired
    private RedisDao redisDao;

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




}
