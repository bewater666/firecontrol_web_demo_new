package com.orient.firecontrol_web_demo.config.rabbit;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/11 14:08
 * @func rabbitMQ的配置类
 */
@Configuration
public class RabbitConfig {
    public static final String QUEUE_A = "queue_ff";
    public static final String QUEUE_B = "queue_40";
    public static final String QUEUE_C = "queue_41";
    public static final String QUEUE_D = "queue_51";


    @Bean
    public Queue queue_ff(){
        return new Queue(QUEUE_A,true);
    }

    @Bean
    public Queue queue_40(){
        return new Queue(QUEUE_B,true);
    }

    @Bean
    public Queue queue_41(){
        return new Queue(QUEUE_C,true);
    }

    @Bean
    public Queue queue_51(){
        return new Queue(QUEUE_D,true);
    }

    @Bean
    TopicExchange exchange(){
        return new TopicExchange("topicExchange");
    }

    @Bean
    Binding bindingExchangeMessageff(){
        return BindingBuilder.bind(queue_ff()).to(exchange()).with("topic.ff");
    }

    @Bean
    Binding bindingExchangeMessage40(){
        return BindingBuilder.bind(queue_40()).to(exchange()).with("topic.40");
    }

    @Bean
    Binding bindingExchangeMessage41(){
        return BindingBuilder.bind(queue_41()).to(exchange()).with("topic.41");
    }

    @Bean
    Binding bindingExchangeMessage51(){
        return BindingBuilder.bind(queue_51()).to(exchange()).with("topic.51");
    }


    @Autowired
    private CachingConnectionFactory connectionFactory;
    @Autowired
    private TopicAckReceiver topicAckReceiver;//消息接收处理类

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // RabbitMQ默认是自动确认，这里改为手动确认消息

        container.setQueues(queue_ff(),queue_40(),queue_41(),queue_51());
        container.setMessageListener(topicAckReceiver);

//        container.setPrefetchCount(2);//批量读取消息  一次消费50条

        return container;
    }


}
