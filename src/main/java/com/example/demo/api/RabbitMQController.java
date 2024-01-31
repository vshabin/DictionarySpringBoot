package com.example.demo.api;

import com.example.demo.domain.rabbitMQ.DirectMessageModel;
import com.example.demo.domainservices.WordService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.demo.rabbitMQ.RabbitMQConst.DIRECT_EXCHANGE;


@RestController
@RequestMapping("/rabbit")
@Validated
@Log4j2
public class RabbitMQController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private WordService wordService;

    @PostMapping("/emit")
    public ResponseEntity<String> emit(@Valid @RequestBody DirectMessageModel message) {
        rabbitTemplate.setExchange(DIRECT_EXCHANGE);
        rabbitTemplate.convertAndSend(message.getQueueName(), message.getMessage());
        return ResponseEntity.ok("Message sent");
    }
}
