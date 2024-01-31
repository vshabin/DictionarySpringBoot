package com.example.demo.rabbitMQ;


import com.example.demo.domainservices.TelegramBot;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import static com.example.demo.rabbitMQ.RabbitMQConst.DEFAULT_QUEUE_NAME;
import static com.example.demo.rabbitMQ.RabbitMQConst.TELEGRAM_QUEUE_NAME;

@Component
@Log4j2
@EnableRabbit
public class RabbitMQListener {
    @Autowired
    private TelegramBot telegramBot;

    @RabbitListener(queues = DEFAULT_QUEUE_NAME)
    public void processDefaultMessage(String message) {
        log.info("Received message: " + message);
    }

    @RabbitListener(queues = TELEGRAM_QUEUE_NAME)
    public void processTelegramMessage(String message) {
        var resultMessage = String.format("Наши дорогие администраторы хотят сказать всем фанатам : %s !!!", message);
        telegramBot.sendMessage(resultMessage);
    }
}
