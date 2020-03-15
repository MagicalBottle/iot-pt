package com;

import com.netty.SensorServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private SensorServer sensorServer;

    /**
    *   @desc : netty跟随sptinrboot启动
    *   @auth : TYF
    *   @date : 2020/3/15 - 14:41
    */
    @Override
    public void run(String... args) throws Exception {
        ChannelFuture future = sensorServer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            sensorServer.destroy();
        }));
        future.channel().closeFuture().syncUninterruptibly();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
