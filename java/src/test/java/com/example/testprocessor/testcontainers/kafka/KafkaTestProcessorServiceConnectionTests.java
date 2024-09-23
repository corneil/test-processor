package com.example.testprocessor.testcontainers.kafka;

import com.example.testprocessor.testcontainers.kafka.KafkaTestProcessorServiceConnectionTests.TestServiceConnectionsConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(TestServiceConnectionsConfiguration.class)
@Testcontainers
class KafkaTestProcessorServiceConnectionTests extends KafkaTestProcessorTestBase {

    @TestConfiguration(proxyBeanMethods = false)
    static class TestServiceConnectionsConfiguration {
        @Bean
        @ServiceConnection
        public KafkaContainer kafkaContainer() {
            KafkaContainer kafkaContainer = createKafkaContainer();
            logger.info("kafkaContainer:created:{}", kafkaContainer.getDockerImageName());
            return kafkaContainer;
        }
    }
}
