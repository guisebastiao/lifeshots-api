package com.guisebastiao.lifeshotsapi.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailQueueConfig {
    public static final String MAIL_EXCHANGE = "mail.exchange";
    public static final String MAIL_QUEUE = "mail.queue";
    public static final String MAIL_ROUTING_KEY = "mail.routing-key";

    @Bean
    public TopicExchange mailExchange() {
        return new TopicExchange(MAIL_EXCHANGE);
    }

    @Bean
    public Queue mailQueue() {
        return QueueBuilder.durable(MAIL_QUEUE).build();
    }

    @Bean
    public Binding passwordRecoveryBinding(Queue mailQueue, TopicExchange mailExchange) {
        return BindingBuilder.bind(mailQueue).to(mailExchange).with(MAIL_ROUTING_KEY);
    }
}
