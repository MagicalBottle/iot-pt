package com.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


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
            //host = InetAddress.getLocalHost().getHostAddress();
            host = getPublicHostIp();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return host;
    }

    /**
     *   @desc : 获取公网ip地址(网易公共接口)
     *   @auth : TYF
     *   @date : 2020/3/15 - 11:14
     */
    public static String getPublicHostIp() throws Exception{
        HttpGet get = new HttpGet("http://pv.sohu.com/cityjson");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(get);
        HttpEntity resEntity = response.getEntity();
        String res = EntityUtils.toString(resEntity,"UTF-8");
        String cip = JSONObject.parseObject(new StringBuilder("{").append(res.substring(res.indexOf("{")+1,res.indexOf("}"))).append("}").toString()).getString("cip");
        return cip;
    }

}
