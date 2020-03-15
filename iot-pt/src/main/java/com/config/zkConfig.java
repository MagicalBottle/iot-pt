package com.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
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

    
    /**
    *   @desc : zk会话创建
    *   @auth : TYF
    *   @date : 2020/3/15 - 15:13
    */
    @Bean
    public CuratorFramework getCuratorFramework(){
        //连接重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(address,retryPolicy);
        client.start();
        if(client.isStarted()){
            logger.info("zookeeper session create success ..");
        }else {
            logger.info("zookeeper session create fail ..");
        }
        return client;
    }

}
