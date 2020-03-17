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

import java.util.*;

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
    public Map<Integer,String> getAllOnlinePT(){
        Map<Integer,String> res = new TreeMap<>();
        try {
            //获取所有节点
            zkClient.getChildren().forPath(parentPath).stream().forEach(node->{
                try {
                    //分别读取节点值
                    Integer count = Integer.valueOf(new String(zkClient.getData().storingStatIn(new Stat()).forPath(parentPath+"/"+node)));
                    res.put(count,node);
                }catch (Exception e){
                    logger.info("获取所有节点失败");
                }
            });
        }catch (Exception e){
               logger.info("获取所有节点失败");
        }
        return res;
    }


    /**
    *   @desc : 获取一个在线pt节点
    *   @auth : TYF
    *   @date : 2020-03-16 - 16:35
    */
    @Override
    public String getOneOnlinePT() throws Exception {
        //所有节点
        Map<Integer,String> nodes = this.getAllOnlinePT();
        if(nodes.keySet().size()<=0){
            return null;
        }
        //最小负载节点
        Integer key = Collections.min(nodes.keySet());
        String host = nodes.get(key);
        logger.info("当前最小负载节点"+host+"客户端"+key+"个");
        return host;
    }


    /**
    *   @desc : 获取并缓存一个token
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:46
    */
    @Override
    public String getCachedToken(Long clientId) {
        String token = UUID.randomUUID().toString();
        redisDao.setString(tokenPrefix+token,String.valueOf(clientId),60);//60秒过期
        return token;
    }
}
