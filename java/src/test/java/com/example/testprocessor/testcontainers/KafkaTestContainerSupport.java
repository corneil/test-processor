package com.example.testprocessor.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class KafkaTestContainerSupport {
    private static final Logger logger = LoggerFactory.getLogger("kafka");
    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
        .withLogConsumer((logMessage) -> {
            switch (logMessage.getType()) {
                case STDERR -> logger.error(logMessage.getUtf8StringWithoutLineEnding());
                case STDOUT -> logger.info(logMessage.getUtf8StringWithoutLineEnding());
            }
        });

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
}
