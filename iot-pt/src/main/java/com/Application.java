package com;

import com.netty.PTServer;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PTServer PTServer;

    /**
    *   @desc : netty跟随sptinrboot启动
    *   @auth : TYF
    *   @date : 2020/3/15 - 14:41
    */
    @Override
    public void run(String... args) throws Exception {
        ChannelFuture future = PTServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("进入jvm关闭钩子");
            PTServer.destroy();
        }));
        future.channel().closeFuture().sync();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
