package com.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    @Value("${pt.server.zk.path}")
    private String parentPath;

    @Value("${client.login.redis.prefix}")
    private String loginPrefix;

    @Value("${client.heart.redis.prefix}")
    private String heartPrefix;

    @Value("${client.token.redis.prefix}")
    private String tokenPrefix;

    @Value("${client.count.redis.prefix}")
    private String clientCountPrefix;

    @Autowired
    private CuratorFramework zkClient;

    @Autowired
    private RedisDao redisDao;


    /**
    *   @desc : 获取所有在线pt节点,  客户端个数-节点名称
    *   @auth : TYF
    *   @date : 2020/3/15 - 19:02
    */
    @Override
    public Map<Integer,String> getAllOnlinePT(){
        Map<Integer,String> res = new TreeMap<>();
        try {
            //获取所有节点
            zkClient.getChildren().forPath(parentPath).stream().forEach(node->{
                //分别读取节点客户端数量
                String value = redisDao.getString(clientCountPrefix+node);
                if(value!=null){
                    Integer count = Integer.valueOf(value);
                    logger.info("节点"+node+"客户端数量"+count);
                    res.put(count,node);
                }
            });
        }catch (Exception e){
               logger.info("获取所有节点失败");
        }
        return res;
    }


    /**
     *   @desc : 获取所有在线pt节点,  节点名称-客户端个数
     *   @auth : TYF
     *   @date : 2020/3/15 - 19:02
     */
    @Override
    public Map<String,Integer> getAllOnlinePT2(){
        Map<String,Integer> res = new TreeMap<>();
        try {
            //获取所有节点
            zkClient.getChildren().forPath(parentPath).stream().forEach(node->{
                //分别读取节点客户端数量
                String value = redisDao.getString(clientCountPrefix+node);
                if(value!=null){
                    Integer count = Integer.valueOf(value);
                    logger.info("节点"+node+"客户端数量"+count);
                    res.put(node,count);
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
        //60秒过期
        redisDao.setString(tokenPrefix+token,String.valueOf(clientId),60);
        return token;
    }


    /**
    *   @desc : 获取pt-server节点状态信息
    *   @auth : TYF
    *   @date : 2020-03-18 - 16:56
    */
    @Override
    public JSONObject getPtStatus() {

        //获取在线节点,以及在线节点的客户端数量
        Map<String,Integer> nodes = getAllOnlinePT2();
        JSONObject res = new JSONObject();
        nodes.entrySet().stream().forEach(entry->{
            res.put(entry.getKey(),entry.getValue());
        });
        return res;

    }


    /**
    *   @desc : 获取所有客户端在线情况
    *   @auth : TYF
    *   @date : 2020-03-18 - 17:08
    */
    @Override
    public JSONObject getClientHeart() {

        return null;

    }



    /**
     *   @desc : 获取所有客户端连接所在服务器情况
     *   @auth : TYF
     *   @date : 2020-03-18 - 17:08
     */
    @Override
    public JSONObject getClientConn() {

        return null;

    }

}
