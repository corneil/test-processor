package com.example.testprocessor.testcontainers.rabbit;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class RabbitListenerContainer {
    private static final Logger logger = LoggerFactory.getLogger(RabbitListenerContainer.class);
    private final ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<>();

    @RabbitListener(queues = "output-queue.test-consumer")
    void listener(Message record) {
        logger.info("queued:{}", record);
        messages.add(record);
    }

    boolean hasMessages() {
        return !messages.isEmpty();
    }

    Message fetchMessage() {
        return messages.poll();
    }
}
