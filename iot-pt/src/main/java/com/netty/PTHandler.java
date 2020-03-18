package com.netty;

import com.service.ClientService;
import com.service.LimiterService;
import com.service.PTService;
import com.service.impl.PTServiceImpl;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
    private PTService ptService;

    @Autowired
    private ClientService clientService;

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
        //清除channel缓存
        clientService.deleteChannel(clientService.loadClientId(ctx.channel()));
        //清除限流器
        limiterService.deleteChannelLimiter(clientService.loadClientId(ctx.channel()));
        //清除login缓存
        clientService.deleteLoginInfo(clientService.loadClientId(ctx.channel()));
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
        //清除channel缓存
        clientService.deleteChannel(clientService.loadClientId(ctx.channel()));
        //清除channel限流器
        limiterService.deleteChannelLimiter(clientService.loadClientId(ctx.channel()));
        //清除login缓存
        clientService.deleteLoginInfo(clientService.loadClientId(ctx.channel()));
        super.channelInactive(ctx);
    }

    /**
     *   @desc : 上行消息丢给线程池处理
     *   @auth : TYF
     *   @date : 2020-03-16 - 11:17
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        PTServiceImpl.msgExecutor.execute(()->{
            //消息预处理
            //TODO  线程池拒绝任务提交处理
            ptService.msgPreExecute(ctx.channel(),msg);
        });
        return;
    }


}