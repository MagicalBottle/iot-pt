package com.netty;


import com.service.LimiterService;
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

    @Autowired
    private LimiterService limiterService;

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
        logger.info("上行消息 "+msg);
        if(!limiterService.tryChannelAcquire(ctx.channel())){
            logger.info("达到客户端消息流量限制"+new Date());
        }
        if(!limiterService.tryGlobalAcquire()){
            logger.info("达到全局消息流量限制"+new Date());
        }
    }


}