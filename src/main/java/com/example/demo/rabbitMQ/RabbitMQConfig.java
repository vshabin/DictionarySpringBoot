package com.example.demo.rabbitMQ;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.demo.rabbitMQ.RabbitMQConst.*;


@Configuration
@Log4j2
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }

    @Bean
    public AmqpAdmin AmqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue defaultQueue() {
        return new Queue(DEFAULT_QUEUE_NAME);
    }

    @Bean
    public Queue telegramQueue() {
        return new Queue(TELEGRAM_QUEUE_NAME);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding defaultQueueBinding() {
        return BindingBuilder.bind(defaultQueue()).to(directExchange()).with(DEFAULT_QUEUE_NAME);
    }

    @Bean
    public Binding telegramQueueBinding() {
        return BindingBuilder.bind(telegramQueue()).to(directExchange()).with(TELEGRAM_QUEUE_NAME);
    }

//    @Bean
//    public SimpleMessageListenerContainer messageListenerContainer(){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        container.setQueueNames(QUEUE_NAME);
//        container.setMessageListener(message -> {
//            log.info("Received message: " + new String(message.getBody()));
//        });
//        return container;
//    }

}
