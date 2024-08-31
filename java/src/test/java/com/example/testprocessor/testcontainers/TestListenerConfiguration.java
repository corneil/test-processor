package com.example.testprocessor.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@TestConfiguration(proxyBeanMethods = false)
class TestListenerConfiguration {
    @Bean
    KafkaListenerContainer kafkaListener() {
        return new KafkaListenerContainer();
    }
}
