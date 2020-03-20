package com.utils.amqp;

import com.alibaba.fastjson.JSONObject;
import com.config.AmqpConfig;
import com.service.ClientService;
import com.utils.ClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ClientService clientService;

    /**
     *   @desc : 下行消息处理
     *   @auth : TYF
     *   @date : 2019-10-17 - 14:32
     */
    @RabbitListener(queues=AmqpConfig.downQueueName,containerFactory="customContainerFactory")
    public void istPayRealCallbackQueue(Message mes){

        try {
            JSONObject content = toObject(mes.getBody());
            clientService.msgResp(content);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.info("消息异常,下发客户端失败");
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
