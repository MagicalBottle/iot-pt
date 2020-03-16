package com.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryForever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class zkConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${zk.address}")
    private String address;

    @Value("${zk.session.timeout}")
    private int sessionTimeout;

    @Value("${zk.connect.timeout}")
    private int connectionTimeout;

    /**
    *   @desc : zk会话创建
    *   @auth : TYF
    *   @date : 2020/3/15 - 15:13
    */
    @Bean
    public CuratorFramework getCuratorFramework(){
        //连接重试
        RetryPolicy retryPolicy = new RetryForever(500);
        //会话超时时间尽量小保证临时节点即时删除
        CuratorFramework client = CuratorFrameworkFactory.newClient(address,sessionTimeout,connectionTimeout,retryPolicy);
        client.start();
        if(client.isStarted()){
            logger.info("zookeeper会话创建成功 ..");
        }else {
            logger.info("zookeeper会话创建失败 ..");
        }
        return client;
    }

}
