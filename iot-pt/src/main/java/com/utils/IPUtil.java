package com.utils;


import java.net.InetAddress;

public class IPUtil {

    /**
    *   @desc : 获取本地ip地址
    *   @auth : TYF
    *   @date : 2020/3/15 - 11:14
    */
    public static String getLocalHostIp() {
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e){

        }
        return host;
    }


}
