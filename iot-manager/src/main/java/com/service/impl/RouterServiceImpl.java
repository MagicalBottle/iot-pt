package com.service.impl;

import com.service.RouterService;
import com.utils.redis.RedisDao;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class RouterServiceImpl implements RouterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${pt.server.path}")
    private String parentPath;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private RedisDao redisDao;

    private static final String tokenPrefix = "pt:router:token:";

    /**
    *   @desc : 获取所有在线pt节点
    *   @auth : TYF
    *   @date : 2020/3/15 - 19:02
    */
    @Override
    public List<String> getAllOnlinePT(){
        List<String> nodes = new ArrayList<>();
        //获取所有节点
        try {
            zkClient.getChildren().forPath(parentPath).stream().forEach(n->{
                nodes.add(n);
            });
        }catch (Exception e){
               logger.info("获取所有节点失败");
        }
        logger.info("当前在线节点 "+Arrays.toString(nodes.toArray()));
        return nodes;
    }


    /**
    *   @desc : 获取一个在线pt节点
    *   @auth : TYF
    *   @date : 2020-03-16 - 16:35
    */
    @Override
    public String getOneOnlinePT() throws Exception {
        List<String> nodes = this.getAllOnlinePT();
        return nodes.get(nodes.size()-1);
    }


    /**
    *   @desc : 获取并缓存一个token
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:46
    */
    @Override
    public String getCachedToken(String clientId) {
        String token = UUID.randomUUID().toString();
        redisDao.setString(tokenPrefix+token,clientId,60);//60秒过期
        return token;
    }
}
