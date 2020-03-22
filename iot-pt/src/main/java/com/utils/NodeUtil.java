package com.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class NodeUtil {


    /**
     *   @desc : 获取公网ip地址(网易公共接口)
     *   @auth : TYF
     *   @date : 2020/3/15 - 11:14
     */
    public static String getNettyNodeName(int port) {
        HttpGet get = new HttpGet("http://pv.sohu.com/cityjson");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String cip = "localhost";
        try {
            CloseableHttpResponse response = httpClient.execute(get);
            HttpEntity resEntity = response.getEntity();
            String res = EntityUtils.toString(resEntity,"UTF-8");
            cip = JSONObject.parseObject(new StringBuilder("{").append(res.substring(res.indexOf("{")+1,res.indexOf("}"))).append("}").toString()).getString("cip");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return cip+":"+port;
    }

}
