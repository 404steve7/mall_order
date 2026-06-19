package com.henry.mallorder.order.mq;

import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "order-topic",
        consumerGroup = "mall-order-consumer-group"
)
public class OrderMessageConsumer implements RocketMQListener<String> {
    private static final Logger log = LoggerFactory.getLogger(OrderMessageConsumer.class);

    @Override
    public void onMessage(String orderNo) {
        log.info("receive order created message, orderNo={}", orderNo);
    }
}
