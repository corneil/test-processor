package com.example.testprocessor.testcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class KafkaServiceConnectionSupport {
    private static final Logger logger = LoggerFactory.getLogger("kafka");

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withLogConsumer((logMessage) -> {
                switch (logMessage.getType()) {
                    case STDERR -> logger.error(logMessage.getUtf8StringWithoutLineEnding());
                    case STDOUT -> logger.info(logMessage.getUtf8StringWithoutLineEnding());
                }
            });
    }

}
