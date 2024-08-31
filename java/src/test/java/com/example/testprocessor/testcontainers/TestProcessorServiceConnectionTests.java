package com.example.testprocessor.testcontainers;

import com.example.testprocessor.testcontainers.TestProcessorServiceConnectionTests.TestServiceConnectionsConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;

@Import(TestServiceConnectionsConfiguration.class)
public class TestProcessorServiceConnectionTests extends TestProcessorTestBase {
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
