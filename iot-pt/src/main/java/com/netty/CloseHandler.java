package com.netty;

import com.service.ClientService;
import com.service.LimiterService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
*   @desc : 监听客户端close事件
*   @auth : TYF
*   @date : 2020-03-19 - 10:35
*/
@Component
@ChannelHandler.Sharable
public class CloseHandler extends ChannelOutboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientService clientService;

    @Autowired
    private LimiterService limiterService;

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        String clientId = clientService.loadClientId(ctx.channel());
        logger.info("连接断开,清除系列缓存 clientId="+clientId);
        //清除channel缓存
        clientService.deleteChannel(clientId);
        //清除限流器
        limiterService.deleteChannelLimiter(clientId);
        //清除login缓存
        clientService.deleteLoginInfo(clientId);
        super.close(ctx, promise);
    }






}
