package com.service;

import com.alibaba.fastjson.JSONObject;

public interface MsgUpService {
    void msgHandler(JSONObject msg);
}
