package com.netty;

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
public class ConnHandler extends SimpleChannelInboundHandler<String> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PTService ptService;


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("客户端exceptionCaught退出连接:"+ctx.channel().remoteAddress().toString());
        logger.info(cause.toString());
        //禁用handler的ctx.close()以将close事件进行传递
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端channelActive主动连接:"+ctx.channel().remoteAddress().toString());
        ctx.fireChannelActive();
        ctx.flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端channelInactive退出连接:"+ctx.channel().remoteAddress().toString());
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