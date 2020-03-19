package com.netty;

import com.service.ClientService;
import com.service.impl.ClientServiceImpl;
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
    private ClientService clientService;



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("客户端exceptionCaught退出连接:"+ctx.channel().remoteAddress().toString());
        logger.info(cause.toString());
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端channelActive主动连接:"+ctx.channel().remoteAddress().toString());
        ctx.fireChannelActive();
        ctx.flush();
    }


    /**
    *   @desc : 有并发问题,比如登陆成功后立即掉线->删除缓存->添加缓存的顺序,暂用缓存延迟清空的方式来解决
    *   @auth : TYF
    *   @date : 2020-03-19 - 15:31
    */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientId = clientService.loadClientId(ctx.channel());
        logger.info("客户端退出连接,清除系列缓存 clientId:"+clientId);
        clientService.removeCache(clientId);
        super.channelInactive(ctx);
    }



    /**
     *   @desc : 上行消息丢给线程池处理
     *   @auth : TYF
     *   @date : 2020-03-16 - 11:17
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        ClientServiceImpl.msgExecutor.execute(()->{
            //消息预处理
            //TODO  线程池拒绝任务提交处理
            clientService.msgPreExecute(ctx.channel(),msg);
        });
        return;
    }


}