package com.utils.amqp;

import com.alibaba.fastjson.JSONObject;
import com.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     *   @desc : 下行消息处理,各个netty节点监听各自队列的消息
     *   @auth : TYF
     *   @date : 2019-10-17 - 14:32
     */
    @RabbitListener(queues="#{downQueue.name}",containerFactory="customContainerFactory")
    public void istPayRealCallbackQueue(Message mes){
        try {
            JSONObject content = JSONObject.parseObject(new String(mes.getBody()));
            clientService.msgResp(content);
        }
        catch (Exception e){
            e.printStackTrace();
            logger.info("消息异常,下发客户端失败");
        }
    }


}
