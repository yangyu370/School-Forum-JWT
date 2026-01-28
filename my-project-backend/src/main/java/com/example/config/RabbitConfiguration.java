package com.example.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean("errorQueue")
    public Queue dlQueue() {
        return QueueBuilder.durable("error")
                .ttl(24*60*60*1000)
                .build();
    }
    @Bean("errorExchange")
    public Exchange dlExchange() {
        return ExchangeBuilder
                .directExchange("dlx.direct")
                .build();
    }
    @Bean
    public Binding dlBinding(@Qualifier("errorQueue") Queue dlQueue,
                             @Qualifier("errorExchange") Exchange dlExchange) {
        return BindingBuilder
                .bind(dlQueue)
                .to(dlExchange)
                .with("error-message")
                .noargs();
    }
    @Bean("emailQueue")
    public Queue emailqueue() {
        return QueueBuilder
                .durable("mail")
                .deadLetterExchange("dlx.direct")
                .deadLetterRoutingKey("error-message")
                .ttl(3*60*60*1000)
                .build();
    }
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
