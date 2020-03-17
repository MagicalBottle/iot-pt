package com.utils;

import com.alibaba.fastjson.JSONObject;

public class CommonMsgResult {


    public static JSONObject getFailMsg(Integer state,String msg){
        JSONObject res = new JSONObject();
        res.put("state",state);
        res.put("msg",msg);
        return res;
    }


}
