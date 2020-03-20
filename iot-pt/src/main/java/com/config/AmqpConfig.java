package com.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
*   @desc : rabbitmq配置类
*   @auth : TYF
*   @data : 2019-01-21 - 16:19
*/
@Configuration
public class AmqpConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.username}")
    private String name;

    @Value("${spring.rabbitmq.password}")
    private String pw;

    @Value("${spring.rabbitmq.thread.max}")
    private Integer max;

    @Value("${spring.rabbitmq.thread.min}")
    private Integer min;


    public final static String exchangeName = "msg_handler_exchange";

    public final static String downQueueName = "msg_handler_queue_down";

    public final static String upQueueName = "msg_handler_queue_up";


    //连接配置
    @Bean
    public ConnectionFactory connectionFactory() {
        logger.info("rabbitmq客户端启动..");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(host+":"+port);
        connectionFactory.setUsername(name);
        connectionFactory.setPassword(pw);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    //多线程消费处理
    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(min);
        factory.setMaxConcurrentConsumers(max);
        configurer.configure(factory, connectionFactory);
        return factory;
    }


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }


    //声明交换机
    @Bean
    public DirectExchange msgHandlerExchange(){
        return new DirectExchange(exchangeName);
    }

    //队列(上行消息)
    @Bean
    public Queue upQueue(){
        return new Queue(upQueueName,true);
    }
    @Bean
    public Binding upQueueBind(){
        return BindingBuilder.bind(upQueue()).to(msgHandlerExchange()).with(upQueueName);
    }


    //队列(下行消息)
    @Bean
    public Queue downQueue(){
        return new Queue(downQueueName,true);
    }
    @Bean
    public Binding downQueueBind(){
        return BindingBuilder.bind(downQueue()).to(msgHandlerExchange()).with(downQueueName);
    }




}
