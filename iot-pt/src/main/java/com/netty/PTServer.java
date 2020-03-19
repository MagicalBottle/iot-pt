package com.netty;

import com.service.PTService;
import com.service.impl.ClientServiceImpl;
import com.utils.IPUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;


@Component
public class PTServer {

    @Value("${netty.port}")
    private int port;
    @Autowired
    private PTService ptService;
    @Autowired
    private ConnHandler connHandler;
    @Autowired
    private CloseHandler closeHandler;

    private  Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Channel channel;

    /**
    *   @desc : netty启动器
    *   @auth : TYF
    *   @date : 2020/3/15 - 14:42
    */
    public ChannelFuture start() throws Exception {

        ChannelFuture f = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    //关闭监听保证客户端缓存能够清除
                                    .addLast(closeHandler)
                                    //解决TCP拆包
                                    .addLast(new LineBasedFrameDecoder(1024))
                                    //客户端消息反序列化
                                    .addLast(new StringDecoder(Charset.forName("UTF-8")))
                                    //客户端35s无新消息踢掉连接
                                    .addLast(new ReadTimeoutHandler(35))
                                    //业务消息处理
                                    .addLast(connHandler);
                        }
                    });
            f = b.bind().sync();
            channel = f.channel();
            logger.info("netty开始启动 ..");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("netty启动失败 ..");
        } finally {
            if (f != null && f.isSuccess()) {
                logger.info("netty启动成功 .. ");
                registry();//注册到zk
                clientCountReport();//定时上报客户端数量
            } else {
                logger.info("netty启动失败 ..");
            }
        }
        return f;
    }


    /**
    *   @desc : netty跟随springboot销毁
    *   @auth : TYF
    *   @date : 2020/3/15 - 14:43
    */
    public void destroy() {
        logger.info("netty开始销毁 ..");
        if(channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        logger.info("netty销毁成功 ..");
    }


    /**
    *   @desc : 注册到zk上去
    *   @auth : TYF
    *   @date : 2020-03-16 - 10:10
    */
    public void registry(){
        new Thread(()->{
            try {
                String addr = IPUtil.getLocalHostIp();
                ptService.registryToZk(addr,port);
                logger.info("netty注册成功 ..");
            }catch (Exception e){
                e.printStackTrace();
                logger.info("netty注册失败 ..");
            }
        }).start();
    }

    /**
    *   @desc : 客户端数量定时上报
    *   @auth : TYF
    *   @date : 2020-03-17 - 13:19
    */
    public void clientCountReport(){
        Integer expire = 15;//上报间隔时间
        new Thread(()->{
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run() {
                    try {
                        String addr = IPUtil.getLocalHostIp();
                        int count = ClientServiceImpl.getChannelMap().size();
                        ptService.clientCountReport(addr,port,count,expire);
                    }catch (Exception e){
                        e.printStackTrace();
                        logger.info("客户端数量定时上报失败.");
                    }
                }
            }, 10000, expire*1000);//15s一次
        }).start();
    }


}
