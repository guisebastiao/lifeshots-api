package com.guisebastiao.lifeshotsapi.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushQueueConfig {
    public static final String PUSH_QUEUE  = "push.queue";
    public static final String PUSH_EXCHANGE = "push.exchange";
    public static final String PUSH_ROUTING_KEY = "push.routing.key";

    @Bean
    public TopicExchange pushExchange() {
        return new TopicExchange(PUSH_EXCHANGE);
    }

    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable(PUSH_QUEUE).build();
    }

    @Bean
    public Binding pushBinding(Queue pushQueue, TopicExchange pushExchange) {
        return BindingBuilder.bind(pushQueue).to(pushExchange).with(PUSH_ROUTING_KEY);
    }
}
