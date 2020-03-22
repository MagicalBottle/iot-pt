package com.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
*   @desc : 登陆缓存处理
*   @auth : TYF
*   @date : 2020-03-19 - 16:29
*/
@Service
public class LoginUtil {

    @Autowired
    private RedisUtil redisUtil;

    //redis登陆信息缓存前缀
    @Value("${client.login.redis.prefix}")
    private String loginPrefix;

    //netty启动端口
    @Value("${netty.port}")
    private int port;

    /**
     *   @desc : 缓存登陆信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    public void saveLoginInfo(String clientId) {
        String key = loginPrefix+clientId;
        String nodeName = NodeUtil.getNettyNodeName(port);
        redisUtil.setString(key,nodeName);
    }

    /**
     *   @desc : 清除登录信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    public void deleteLoginInfo(String clientId) {
        String key = loginPrefix+clientId;
        redisUtil.delString(key);
    }

}
