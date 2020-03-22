package com.utils.amqp;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
*   @desc : rabbitmq即时消息发送
*   @auth : TYF
*   @data : 2019-01-21 - 16:43
*/
@Component
public class AmqpSender implements RabbitTemplate.ConfirmCallback{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public AmqpSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
    *   @desc : broker确认的生产者回调
    *   @auth : TYF
    *   @data : 2019-01-21 - 16:45
    */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

    }

    /**
    *   @desc : 向指定交换机/队列发送即时消息
    *   @auth : TYF
    *   @data : 2019-01-21 - 16:46
    */
    public void sendMsg(String exchange,String queue,JSONObject centent){
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                exchange,
                queue,
                centent.toJSONString().getBytes(),
                correlationId
        );
    }




}
