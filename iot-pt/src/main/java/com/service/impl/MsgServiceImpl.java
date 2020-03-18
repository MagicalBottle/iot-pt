package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.service.ClientService;
import com.service.LimiterService;
import com.service.MsgService;
import com.utils.IPUtil;
import com.utils.redis.RedisDao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MsgServiceImpl implements MsgService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    //最大客户端连接数
    @Value("${netty.client.size.max}")
    private Integer clientMaxSize;


    //客户端管理器
    @Autowired
    private ClientService clientService;

    //客户端限流管理器
    @Autowired
    private LimiterService limiterService;



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

        //重复登陆验证 TODO


        //缓存通道对象(cahnnel)和登陆信息(redis)
        clientService.saveChannel(channel,clientId);
        clientService.saveLoginInfo(channel);
        //创建限流器
        limiterService.saveChannelLimiter(clientId);
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
        String clientId = clientService.loadClientId(channel);
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
        String clientId = clientService.loadClientId(channel);
        //客户端未登陆
        if(clientId==null){
            logger.info("未登录,请别发心跳!");
            clientError(channel,"未登录,请别发心跳",heart);
            channel.close();
        }
        //客户端已登陆
        else{
            //缓存心跳时间
            clientService.saveHeartInfo(clientId);
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

}
