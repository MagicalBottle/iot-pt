package com.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.utils.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    private ClientUtil clientUtil;

    @Autowired
    private LimiterUtil limiterUtil;

    @Autowired
    private HeartUtil heartUtil;

    @Autowired
    private LoginUtil loginUtil;



    //最大客户端连接数
    @Value("${netty.client.size.max}")
    private Integer clientMaxSize;




    //消息处理线程池
    public static ExecutorService msgExecutor = new ThreadPoolExecutor(150, 300,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(2000));

    //缓存处理线程池
    public static ExecutorService cacheExecutor = new ThreadPoolExecutor(25, 50,60L, TimeUnit.SECONDS,new ArrayBlockingQueue(2000));


    /**
     *   @desc : 保存登陆缓存
     *   @auth : TYF
     *   @date : 2020-03-19 - 15:47
     */
    @Override
    public void saveCache(Channel channel, String clientId) {
        //缓存通道信息
        clientUtil.saveChannel(channel,clientId);
        //缓存登陆信息
        loginUtil.saveLoginInfo(clientId);
        //创建限流器
        limiterUtil.saveChannelLimiter(clientId);
    }

    /**
     *   @desc : 清除登陆缓存
     *   @auth : TYF
     *   @date : 2020-03-19 - 15:47
     */
    @Override
    public void removeCache(String clientId) {
        //清除channel缓存
        clientUtil.deleteChannel(clientId);
        //清除限流器
        limiterUtil.deleteChannelLimiter(clientId);
        //清除login缓存
        loginUtil.deleteLoginInfo(clientId);
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
        if(ClientUtil.getChannelMap().size()>=clientMaxSize){
            msgResp(channel,MsgUtil.commonMsg(0,"当前节点负载上限拒绝连接",msg.toString()).toJSONString());
            channel.close();
            logger.info("当前服务器节点负载上限拒绝连接 clientId="+clientId);
            return;
        }
        //缓存登陆
        saveCache(channel,clientId);
        //返回登录响应
        msgResp(channel,MsgUtil.commonMsg(1,"登陆成功",msg.toString()).toJSONString());
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
        String clientId = clientUtil.loadClientId(channel);
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
        String clientId = clientUtil.loadClientId(channel);
        //客户端未登陆
        if(clientId==null){
            logger.info("未登录,请别发心跳!");
            msgResp(channel,MsgUtil.commonMsg(0,"未登录,请别发心跳",heart).toJSONString());
            channel.close();
        }
        //客户端已登陆
        else{
            //缓存心跳时间
            logger.info("客户端心跳 clientId"+clientId+",heart="+heart);
            heartUtil.saveHeartInfo(clientId);
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
            msgResp(channel,MsgUtil.commonMsg(0,"客户端消息为空",msg).toJSONString());
            return;
        }

        //全局消息限流
        if(!limiterUtil.tryGlobalAcquire()){
            logger.info("触发全局限流,请重试");
            msgResp(channel,MsgUtil.commonMsg(0,"触发全局限流,请重试",msg).toJSONString());
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
                msgResp(channel,MsgUtil.commonMsg(0,"消息格式非标准json",msg).toJSONString());
                return;
            }
        }

        //命令名称
        String serviceName = jObj.getString("service_name");
        //客户端编号
        String clientId = jObj.getString("client_id");
        if(!StringUtils.isNotNull(serviceName)||!StringUtils.isNotNull(clientId)){
            logger.info("消息缺少必传字段,serviceName="+serviceName+",clientId="+clientId);
            msgResp(channel,MsgUtil.commonMsg(0,"消息缺少必传字段",msg).toJSONString());
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
            msgResp(channel,MsgUtil.commonMsg(0,"请先登陆",msg).toJSONString());
            return;
        }
        //正常业务消息
        else{
            //全局消息限流
            if(!limiterUtil.tryChannelAcquire(clientId)){
                logger.info("触发客户端限流,请重试");
                msgResp(channel,MsgUtil.commonMsg(0,"触发客户端限流,请重试",msg).toJSONString());
                return;
            }
            clientMsgReSend(channel,jObj);
            return;
        }


    }

}
