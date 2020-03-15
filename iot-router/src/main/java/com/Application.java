package com;


import com.service.RouterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@RestController
@RequestMapping("/test")
public class Application{

    @Autowired
    private RouterService routerService;


    @RequestMapping("/1")
    public String test() throws Exception{
        return Arrays.toString(routerService.getOnlineAllSensor().toArray());
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
