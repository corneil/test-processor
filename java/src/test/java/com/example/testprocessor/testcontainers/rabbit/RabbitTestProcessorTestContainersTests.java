package com.example.testprocessor.testcontainers.rabbit;


import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Testcontainers
public class RabbitTestProcessorTestContainersTests extends RabbitTestProcessorTestBase {
    @Container
    static RabbitMQContainer rabbitMQContainer = createRabbitMQContainer();

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.addresses", rabbitMQContainer::getAmqpUrl);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }
    @BeforeAll
    static void setup() {
        logger.info("starting:{}", rabbitMQContainer.getDockerImageName());
        rabbitMQContainer.start();
        logger.info("started:{}", rabbitMQContainer.getContainerId());
    }
}
