package com.orient.firecontrol_web_demo.config.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author bewater
 * @version 1.0
 * @date 2019/10/12 11:14
 * @func
 */
@Slf4j
@Component
public class SendCommand {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //50 下发反控指令
    public void send50(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.50", msg);
    }


    //60 下发对时指令
    public void send60(String msg){
        rabbitTemplate.convertAndSend("topicExchange", "topic.60", msg);
    }



}
