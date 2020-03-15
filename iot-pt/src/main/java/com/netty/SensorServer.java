package com.netty;

import com.service.SensorRegistryService;
import com.utils.IPUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;


@Component
public class SensorServer {

    @Value("${netty.port}")
    private int port;
    @Autowired
    private SensorRegistryService registryService;

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

        final SensorHandler serverHandler = new SensorHandler();
        ChannelFuture f = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            f = b.bind().sync();
            channel = f.channel();
            logger.info("netty start ..");
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("netty start fail ..");
        } finally {
            if (f != null && f.isSuccess()) {
                logger.info("netty start success .. ");
                registry();
            } else {
                logger.info("netty start fail ..");
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
        logger.info("netty destory start ..");
        if(channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        logger.info("netty destory success ..");
    }


    public void registry(){
        logger.info("registry to zookeeper ..");
        try {
            String addr = IPUtil.getLocalHostIp();
            logger.info(addr+":"+port);
            registryService.registryToZk(addr,port);
            logger.info("registry to zookeeper success ..");
        }catch (Exception e){
            e.printStackTrace();
            logger.info("registry to zookeeper fail ..");
        }
    }

}
