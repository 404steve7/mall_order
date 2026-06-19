package com.henry.mallorder.order.mq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageProducer {

    private static final String ORDER_TOPIC = "order-topic";

    private final RocketMQTemplate rocketMQTemplate;

    public OrderMessageProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void sendOrderCreatedMessage(String orderNo) {
        rocketMQTemplate.convertAndSend(ORDER_TOPIC, orderNo);
    }

}
