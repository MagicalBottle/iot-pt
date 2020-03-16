package com.netty;


import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import com.service.LimiterService;
import com.service.MsgService;
import com.service.impl.MsgServiceImpl;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
*   @desc : 业务消息处理
*   @auth : TYF
*   @date : 2020-03-16 - 11:16
*/
@Component
@ChannelHandler.Sharable
public class PTHandler extends SimpleChannelInboundHandler<String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //消息限流器
    @Autowired
    private LimiterService limiterService;

    //客户端处理器
    @Autowired
    private ClientService clientService;

    //消息处理器
    @Autowired
    private MsgService msgService;

    /**
     *   @desc : 客户端异常退出连接
     *   @auth : TYF
     *   @date : 2020-03-16 - 11:21
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("客户端异常退出连接:"+ctx.channel().remoteAddress().toString());
        logger.info(cause.getMessage());
        ctx.close();
    }

    /**
    *   @desc : 客户端主动连接
    *   @auth : TYF
    *   @date : 2020-03-16 - 11:21
    */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端主动连接:"+ctx.channel().remoteAddress().toString());
        ctx.fireChannelActive();
        ctx.flush();
    }


    /**
     *   @desc : 客户端主动退出连接
     *   @auth : TYF
     *   @date : 2020-03-16 - 11:21
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端主动退出连接:"+ctx.channel().remoteAddress().toString());
        super.channelInactive(ctx);
    }

    /**
     *   @desc : 上行消息丢给线程池处理
     *   @auth : TYF
     *   @date : 2020-03-16 - 11:17
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("上行消息 "+msg+",channelId "+ctx.channel().id());
        MsgServiceImpl.msgExecutor.execute(()->msgService.msgHandler(ctx.channel(),msg));
        return;
    }


}