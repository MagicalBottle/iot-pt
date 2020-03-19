package com.service;


import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface RouterService {

    Map<Integer,String> getAllOnlinePT();

    Map<String,Integer> getAllOnlinePT2();

    String getOneOnlinePT() throws Exception;

    String getCachedToken(Long clientId);

    JSONObject getPtStatus();

    JSONObject getClientHeart();

    JSONObject getClientConn();

}
