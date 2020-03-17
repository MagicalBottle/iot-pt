package com.service.impl;

import com.service.PTService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PTServiceImpl implements PTService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    @Value("${pt.server.path}")
    private String parentPath;


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
}
