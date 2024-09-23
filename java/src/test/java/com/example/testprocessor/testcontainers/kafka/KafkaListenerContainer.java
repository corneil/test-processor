package com.example.testprocessor.testcontainers.kafka;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

class KafkaListenerContainer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerContainer.class);
    private final ConcurrentLinkedQueue<ConsumerRecord<String, Object>> messages = new ConcurrentLinkedQueue<>();

    @KafkaListener(topics = "output-queue", groupId = "test-consumer")
    void listener(ConsumerRecord<String, Object> record) {
        logger.info("queued:{}", record);
        messages.add(record);
    }
    boolean hasMessages() { return !messages.isEmpty(); }
    ConsumerRecord<String, Object> fetchMessage() { return messages.poll(); }
}
