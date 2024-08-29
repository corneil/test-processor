package com.example.testprocessor.testcontainers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import java.util.concurrent.ConcurrentLinkedQueue

class KafkaListenerContainer(val messages: ConcurrentLinkedQueue<ConsumerRecord<String, String>> = ConcurrentLinkedQueue<ConsumerRecord<String, String>>()) {
    companion object {
        val logger = LoggerFactory.getLogger(KafkaListenerContainer::class.java)!!
    }

    @KafkaListener(topics = ["output-queue"], groupId = "test-consumer")
    fun listener(record: ConsumerRecord<String, String>) {
        logger.info("received:{}", record)
        messages.add(record)
    }
}