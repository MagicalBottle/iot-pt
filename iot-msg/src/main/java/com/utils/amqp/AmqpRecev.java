package com.utils.amqp;

import com.alibaba.fastjson.JSONObject;
import com.config.AmqpConfig;
import com.service.MsgUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
*   @desc : rabbitmq即使消息消费
*   @auth : TYF
*   @date : 2020/3/20 - 22:00
*/
@Component
public class AmqpRecev {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MsgUpService upService;

    /**
     *   @desc : 上行消息处理
     *   @auth : TYF
     *   @date : 2019-10-17 - 14:32
     */
    @RabbitListener(queues=AmqpConfig.upQueueName,containerFactory="customContainerFactory")
    public void istPayRealCallbackQueue(Message mes){

        try {
            JSONObject obj = toObject(mes.getBody());
            logger.info("上行消息:"+obj);
            upService.msgHandler(obj);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.info("消息异常,上行处理失败");
        }

    }


    public JSONObject toObject (byte[] bytes) {
        Object obj = null;
        JSONObject msg = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            msg = (JSONObject)obj;
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return msg;
    }


}
