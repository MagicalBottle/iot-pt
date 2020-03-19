package com.netty;

import com.service.ClientService;
import com.service.impl.ClientServiceImpl;
import com.utils.ClientUtil;
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
    private ClientService clientService;

    @Autowired
    private ClientUtil clientUtil;

    /**
    *   @desc : 异常捕获钩子
    *   @auth : TYF
    *   @date : 2020-03-19 - 17:04
    */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("退出连接(exceptionCaught):"+ctx.channel().remoteAddress().toString());
        logger.info(cause.toString());
        ctx.channel().close();
    }

    /**
    *   @desc : 常见连接钩子
    *   @auth : TYF
    *   @date : 2020-03-19 - 17:03
    */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("创建连接(channelActive):"+ctx.channel().remoteAddress().toString());
        ctx.fireChannelActive();
        ctx.flush();
    }


    /**
    *   @desc : 退出连接钩子
    *   @auth : TYF
    *   @date : 2020-03-19 - 15:31
    */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientId = clientUtil.loadClientId(ctx.channel());
        logger.info("退出连接(channelInactive),清除系列缓存 clientId:"+clientId);
        //这里内存溢出风险,登陆时连接断开,先delete后add造成已断开的channel被缓存。
        //目前定期遍历map将无用channel手动清除
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