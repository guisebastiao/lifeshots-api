package com.guisebastiao.lifeshotsapi.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailQueueConfig {

    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String PASSWORD_RECOVERY_QUEUE  = "email.password-recovery.queue";
    public static final String PASSWORD_RECOVERY_ROUTING_KEY = "email.password-recovery";

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Queue passwordRecoveryQueue() {
        return QueueBuilder.durable(PASSWORD_RECOVERY_QUEUE).build();
    }

    @Bean
    public Binding passwordRecoveryBinding(Queue passwordRecoveryQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(passwordRecoveryQueue).to(emailExchange).with(PASSWORD_RECOVERY_ROUTING_KEY);
    }
}
