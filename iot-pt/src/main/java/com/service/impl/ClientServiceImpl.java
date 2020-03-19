package com.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.service.ClientService;
import com.utils.IPUtil;
import com.utils.StringUtils;
import com.utils.redis.RedisDao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.*;

/**
*   @desc : 客户端channel管理
*   @auth : TYF
*   @date : 2020-03-16 - 14:45
*/
@Service
public class ClientServiceImpl implements ClientService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisDao redisDao;

    //redis登陆信息缓存前缀
    @Value("${client.login.redis.prefix}")
    private String loginPrefix;

    //redis心跳信息缓存前缀
    @Value("${client.heart.redis.prefix}")
    private String heartPrefix;

    //netty启动端口
    @Value("${netty.port}")
    private int port;

    //全局每秒令牌数
    @Value("${global.limiter.ticket.count}")
    private Integer globalCount;

    //单独客户端每秒令牌数
    @Value("${channel.limiter.ticket.count}")
    private Integer channelCount;

    //最大客户端连接数
    @Value("${netty.client.size.max}")
    private Integer clientMaxSize;

    //通道缓存
    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    //全局限流器
    private static RateLimiter globalLimiter;

    //通道限流器
    private static Map<String,RateLimiter> channelLimiter = new ConcurrentHashMap<>();

    //消息处理线程池
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(150, 300,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(2000));

    //缓存处理线程池
    public static ExecutorService cacheExecutor = new ThreadPoolExecutor(25, 50,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(2000));


    /**
    *   @desc : 静态参数初始化
    *   @auth : TYF
    *   @date : 2020-03-19 - 16:15
    */
    @PostConstruct
    private void init(){
        globalLimiter = RateLimiter.create(globalCount);
        logger.info("全局客户端消息每秒限制"+globalCount+"条");
    }
    
    /**
    *   @desc : 通过channel查找clientId
    *   @auth : TYF
    *   @date : 2020/3/17 - 19:39
    */
    @Override
    public String loadClientId(Channel channel) {
        AttributeKey<String> key = AttributeKey.valueOf("clientId");
        Attribute<String> attr = channel.attr(key);
        String clientId= attr.get();
        return clientId;
    }


    /**
    *   @desc : 保存client
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:10
    */
    @Override
    public void saveChannel(Channel channel,String clientId) {
        if(clientId==null){
            logger.info("clientId为空保存channel失败");
            return;
        }
        //clientId放到channel内置属性中,方便channel查找clientId
        AttributeKey<String> key = AttributeKey.valueOf("clientId");
        Attribute<String> attr = channel.attr(key);
        attr.set(clientId);
        //再将channel保存到map中
        channelMap.put(clientId,channel);
    }

    /**
    *   @desc : 清除通道缓存
    *   @auth : TYF
    *   @date : 2020/3/17 - 20:14
    */
    @Override
    public void deleteChannel(String clientId) {
        if(clientId!=null){
            channelMap.remove(clientId);
        }
    }


    /**
     *   @desc : 缓存登陆信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    @Override
    public void saveLoginInfo(String clientId) {
        String key = loginPrefix+clientId;
        String value = IPUtil.getLocalHostIp()+":"+port;
        redisDao.setString(key,value);
    }

    /**
     *   @desc : 清除登录信息
     *   @auth : TYF
     *   @date : 2020-03-18 - 13:28
     */
    @Override
    public void deleteLoginInfo(String clientId) {
        String key = loginPrefix+clientId;
        redisDao.delString(key);
    }

    /**
    *   @desc : 缓存心跳信息
    *   @auth : TYF
    *   @date : 2020-03-18 - 13:35
    */
    @Override
    public void saveHeartInfo(String clientId) {
        String key = heartPrefix+clientId;
        //缓存心跳时间1小时过期 需要比协议心跳间隔时间长
        redisDao.setString(key,String.valueOf(System.currentTimeMillis()),60*60);
    }


    /**
     *   @desc : 清除channel限流器
     *   @auth : TYF
     *   @date : 2020/3/17 - 20:49
     */
    @Override
    public void deleteChannelLimiter(String clientId) {
        if(clientId!=null){
            channelLimiter.remove(clientId);
        }
    }

    /**
     *   @desc : 创建限流器
     *   @auth : TYF
     *   @date : 2020-03-18 - 15:04
     */
    @Override
    public void saveChannelLimiter(String clientId) {
        logger.info("单个客户端消息每秒限制"+channelCount+"条");
        channelLimiter.put(clientId, RateLimiter.create(channelCount));
    }

    /**
     *   @desc : 全局客户端消息限流
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    @Override
    public boolean tryGlobalAcquire() {
        return globalLimiter.tryAcquire();
    }

    /**
     *   @desc : 单独客户端消息限流
     *   @auth : TYF
     *   @date : 2020-03-16 - 14:13
     */
    @Override
    public boolean tryChannelAcquire(String clientId) {
        return channelLimiter.get(clientId).tryAcquire();
    }


    /**
     *   @desc : 保存登陆缓存
     *   @auth : TYF
     *   @date : 2020-03-19 - 15:47
     */
    @Override
    public void saveCache(Channel channel, String clientId) {
        //缓存通道信息
        saveChannel(channel,clientId);
        //缓存登陆信息
        saveLoginInfo(clientId);
        //创建限流器
        saveChannelLimiter(clientId);
    }

    /**
     *   @desc : 清除登陆缓存
     *   @auth : TYF
     *   @date : 2020-03-19 - 15:47
     */
    @Override
    public void removeCache(String clientId) {
        //清除channel缓存
        deleteChannel(clientId);
        //清除限流器
        deleteChannelLimiter(clientId);
        //清除login缓存
        deleteLoginInfo(clientId);
    }


    /**
     *   @desc : 返回异常信息
     *   @auth : TYF
     *   @date : 2020-03-17 - 17:20
     */
    @Override
    public void clientError(Channel channel, String msg, String data) {
        JSONObject res = new JSONObject();
        res.put("state",0);
        res.put("msg",msg);
        res.put("req",data);
        msgResp(channel,res.toJSONString());
    }

    /**
     *   @desc : 返回成功信息
     *   @auth : TYF
     *   @date : 2020-03-17 - 17:20
     */
    @Override
    public void clientSuccess(Channel channel, String msg, String data) {
        JSONObject res = new JSONObject();
        res.put("state",1);
        res.put("msg",msg);
        res.put("req",data);
        msgResp(channel,res.toJSONString());
    }

    /**
     *   @desc : 处理客户端登陆
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:33
     */
    @Override
    public void clientLogin(Channel channel, JSONObject msg) {
        String clientId = msg.getString("client_id");
        //不超过客户端最高连接数
        if(ClientServiceImpl.getChannelMap().size()>=clientMaxSize){
            clientError(channel,"当前服务器节点负载上限,请切换节点登陆",msg.toJSONString());
            channel.close();
            logger.info("当前服务器节点负载上限拒绝连接 clientId="+clientId);
            return;
        }
        //如果使用相同的client_id重复登陆 TODO
        //缓存登陆
        saveCache(channel,clientId);
        //返回登录响应
        clientSuccess(channel,"登陆成功",msg.toJSONString());
        logger.info("登陆成功,clientId="+clientId);
        return;
    }



    /**
     *   @desc : 检查客户端是否登录
     *   @auth : TYF
     *   @date : 2020-03-18 - 10:08
     */
    @Override
    public boolean clientIsLogin(Channel channel) {
        String clientId = loadClientId(channel);
        return !(clientId==null);
    }

    /**
     *   @desc : 处理客户端心跳
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:33
     */
    @Override
    public void clientHeart(Channel channel, String heart) {
        //找到clientId
        String clientId = loadClientId(channel);
        //客户端未登陆
        if(clientId==null){
            logger.info("未登录,请别发心跳!");
            clientError(channel,"未登录,请别发心跳",heart);
            channel.close();
        }
        //客户端已登陆
        else{
            //缓存心跳时间
            saveHeartInfo(clientId);
            //返回心跳响应
            msgResp(channel,"0x12");
        }
    }

    /**
     *   @desc : 心跳和登陆以外的上行消息转发给业务处理程序
     *   @auth : TYF
     *   @date : 2020-03-17 - 16:36
     */
    @Override
    public void clientMsgReSend(Channel channel, JSONObject msg) {

    }

    /**
     *   @desc : 消息下行
     *   @auth : TYF
     *   @date : 2020-03-16 - 15:45
     */
    @Override
    public void msgResp(Channel channel, String msg) {
        //通道不可写
        if(channel==null||!channel.isActive()||!channel.isWritable()) {
            logger.info("通道不可写");
            return;
        }
        //消息下行
        if(msg!=null&&!"".equals(msg)){
            try {
                byte[] req = new StringBuffer().append(msg).append("\n").toString().getBytes("UTF-8");
                ByteBuf buf = Unpooled.buffer(req.length);
                buf.writeBytes(req);
                channel.writeAndFlush(buf);
            }catch (Exception e){
                logger.info("消息下发异常");
                return;
            }
        }
        return;
    }



    /**
     *   @desc : 消息预处理(心跳和登陆)
     *   @auth : TYF
     *   @date : 2020-03-16 - 15:24
     */
    @Override
    public void msgPreExecute(Channel channel, String msg) {

        //空消息
        if(msg==null||"".equals(msg)){
            logger.info("客户端消息为空");
            clientError(channel,"客户端消息为空",msg);
            return;
        }

        //全局消息限流
        if(!tryGlobalAcquire()){
            logger.info("触发全局限流,请重试");
            clientError(channel,"触发全局限流,请重试",msg);
            return;
        }

        //心跳消息
        JSONObject jObj;
        if(msg.contains("0x11")){
            clientHeart(channel,msg);
            return;
        }
        //非心跳消息
        else{
            try {
                jObj = JSONObject.parseObject(msg);
            }
            catch (Exception e){
                logger.info("消息格式非标准json");
                clientError(channel,"消息格式非标准json",msg);
                return;
            }
        }

        //命令名称
        String serviceName = jObj.getString("service_name");
        //客户端编号
        String clientId = jObj.getString("client_id");
        if(!StringUtils.isNotNull(serviceName)||!StringUtils.isNotNull(clientId)){
            logger.info("消息缺少必传字段,serviceName="+serviceName+",clientId="+clientId);
            clientError(channel,"缺少必传字段",msg);
            return;
        }

        //登陆消息
        if("login".equals(serviceName)){
            clientLogin(channel,jObj);
            return;
        }
        //非登陆消息 且未登录
        else if(!clientIsLogin(channel)){
            logger.info("请先登陆!");
            clientError(channel,"请先登录!",msg);
            return;
        }
        //正常业务消息
        else{
            clientMsgReSend(channel,jObj);
            return;
        }


    }

}
