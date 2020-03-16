package com.service.impl;

import com.service.RouterService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouterServiceImpl implements RouterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    /**
    *   @desc : 获取所有在线pt节点
    *   @auth : TYF
    *   @date : 2020/3/15 - 19:02
    */
    @Override
    public List<String> getAllOnlinePT() throws Exception{
        logger.info("current online nodes: "+new String(zkClient.getData().storingStatIn(new Stat()).forPath("/zk/nodes")));
        return null;
    }


    /**
    *   @desc : 获取一个在线节点
    *   @auth : TYF
    *   @date : 2020-03-16 - 16:35
    */
    @Override
    public String getOneOnlinePT() throws Exception {
        return null;
    }
}
