package com.example.testprocessor.testcontainers;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestProcessorTestContainersTests extends TestProcessorTestBase {
    @Container
    static KafkaContainer kafkaContainer = createKafkaContainer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
    @BeforeAll
    static void setup() {
        logger.info("starting:{}", kafkaContainer.getDockerImageName());
        kafkaContainer.start();
        logger.info("started:{}", kafkaContainer.getContainerId());
    }
}
