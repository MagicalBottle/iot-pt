package com.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.TimerTask;

/**
*   @desc : 节点服务端处工具类
*   @auth : TYF
*   @date : 2020-03-19 - 16:55
*/
@Service
public class ServerUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${netty.port}")
    private int port;

    @Value("${client.count.redis.prefix}")
    private String clientCountPrefix;

    @Value("${pt.server.zk.path}")
    private String parentPath;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CuratorFramework zkClient;

    /**
    *   @desc : 注册到zk
    *   @auth : TYF
    *   @date : 2020-03-19 - 16:57
    */
    public void registry(){
        String nodeName = NodeUtil.getNettyNodeName(port);
        String childNode = parentPath+"/"+nodeName;
        try {
            zkClient.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(childNode,"online".getBytes());
            logger.info("netty注册成功 ..");
        }
        catch (Exception e){
            e.printStackTrace();
            logger.info("netty注册失败 ..");
        }
    }

    /**
    *   @desc : 负载上报redis
    *   @auth : TYF
    *   @date : 2020-03-19 - 16:58
    */
    public void clientCountReport(){
        //上报间隔时间
        Integer expire = 15;
        Timer timer = new Timer();
        String nodeName = NodeUtil.getNettyNodeName(port);
        String key = clientCountPrefix+nodeName;
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                try {
                    int count = ClientUtil.getChannelMap().size();
                    redisUtil.setString(key,String.valueOf(count),expire+5);
                    logger.info("节点"+nodeName+"客户端"+count+"个");
                }catch (Exception e){
                    e.printStackTrace();
                    logger.info("客户端数量上报失败");
                }
            }
        }, 10000, expire*1000);//15s一次
    }

}
