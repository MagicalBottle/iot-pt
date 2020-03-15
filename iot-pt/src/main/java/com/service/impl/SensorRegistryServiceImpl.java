package com.service.impl;

import com.service.SensorRegistryService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorRegistryServiceImpl implements SensorRegistryService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    /**
    *   @desc : 注册netty服务到zk
    *   @auth : TYF
    *   @date : 2020/3/15 - 15:21
    */
    @Override
    public void registryToZk(String host, int port) throws Exception{

        String data = host+":"+port;
        zkClient.create()
                //根节点
                .creatingParentsIfNeeded()
                //子节点为临时节点,zk会话断开自动清除
                .withMode(CreateMode.EPHEMERAL)
                //节点
                .forPath("/zk/nodes", data.getBytes());
        logger.info("current online nodes: "+new String(zkClient.getData().storingStatIn(new Stat()).forPath("/zk/nodes")));

    }


}
