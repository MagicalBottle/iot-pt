package com.utils;

import com.utils.redis.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
*   @desc : 客户端心跳工具类
*   @auth : TYF
*   @date : 2020-03-19 - 16:26
*/
@Service
public class HeartUtil {

    @Autowired
    private RedisDao redisDao;

    //redis心跳信息缓存前缀
    @Value("${client.heart.redis.prefix}")
    private String heartPrefix;

    /**
     *   @desc : 缓存心跳信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:35
     */
    public void saveHeartInfo(String clientId) {
        String key = heartPrefix+clientId;
        //缓存心跳时间1小时过期 需要比协议心跳间隔时间长
        redisDao.setString(key,String.valueOf(System.currentTimeMillis()),60*60);
    }

}
