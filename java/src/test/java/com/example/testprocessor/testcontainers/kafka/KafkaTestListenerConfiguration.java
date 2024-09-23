package com.example.testprocessor.testcontainers.kafka;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
class KafkaTestListenerConfiguration {
    @Bean
    KafkaListenerContainer kafkaListener() {
        return new KafkaListenerContainer();
    }
}
