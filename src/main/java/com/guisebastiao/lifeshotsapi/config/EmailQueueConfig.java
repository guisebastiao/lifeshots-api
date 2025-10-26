package com.guisebastiao.lifeshotsapi.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
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

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
