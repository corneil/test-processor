package com.example.testprocessor.testcontainers.rabbit;


import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(RabbitTestProcessorServiceConnectionTests.TestServiceConnectionsConfiguration.class)
@Testcontainers
class RabbitTestProcessorServiceConnectionTests extends RabbitTestProcessorTestBase {

    @TestConfiguration(proxyBeanMethods = false)
    static class TestServiceConnectionsConfiguration {
        @Bean
        @ServiceConnection
        public RabbitMQContainer kafkaContainer() {
            RabbitMQContainer rabbitMQContainer = createRabbitMQContainer();
            logger.info("rabbitMQContainer:created:{}", rabbitMQContainer.getDockerImageName());
            return rabbitMQContainer;
        }
    }
}
