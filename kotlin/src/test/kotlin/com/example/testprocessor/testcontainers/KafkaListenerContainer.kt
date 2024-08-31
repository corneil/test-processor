package com.example.testprocessor.testcontainers

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import java.util.concurrent.ConcurrentLinkedQueue

class KafkaListenerContainer {
    companion object {
        val logger = LoggerFactory.getLogger(KafkaListenerContainer::class.java)!!
    }
    private val messages = ConcurrentLinkedQueue<ConsumerRecord<String, Any>>()

    @KafkaListener(topics = ["output-queue"], groupId = "test-consumer")
    fun listener(record: ConsumerRecord<String, Any>) {
        logger.info("queued:{}", record)
        messages.add(record)
    }

    fun hasMessages(): Boolean = messages.isNotEmpty()
    fun fetchMessage(): ConsumerRecord<String, Any> = messages.poll()
}