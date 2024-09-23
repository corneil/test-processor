package com.example.testprocessor.testcontainers.rabbit;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
class RabbitTestListenerConfiguration {
    @Bean
    RabbitListenerContainer rabbitListener() {
        return new RabbitListenerContainer();
    }
}
