package com.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.service.MsgUpService;
import org.springframework.stereotype.Service;


/**
*   @desc : 上行消息处理
*   @auth : TYF
*   @date : 2020/3/22 - 20:10
*/
@Service
public class MsgUpServiceImpl implements MsgUpService {

    /**
    *   @desc : 消息处理器
    *   @auth : TYF
    *   @date : 2020/3/22 - 20:12
    */
    @Override
    public void msgHandler(JSONObject msg) {
        
    }
}
