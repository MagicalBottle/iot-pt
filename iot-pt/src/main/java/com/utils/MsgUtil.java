package com.utils;

import com.alibaba.fastjson.JSONObject;

/**
*   @desc : 付装一些公共的响应详细
*   @auth : TYF
*   @date : 2020-03-19 - 17:09
*/
public class MsgUtil {


    public static JSONObject commonMsg(Integer state,String msg,String sourceMsg){
        JSONObject res = new JSONObject();
        res.put("state",0);
        res.put("msg",msg);
        res.put("req",sourceMsg);
        return res;
    }



}
