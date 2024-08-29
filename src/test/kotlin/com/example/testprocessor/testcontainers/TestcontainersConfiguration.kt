package com.example.testprocessor.testcontainers

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun kafkaContainer(): KafkaContainer {
        return KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withLogConsumer { logConsumer ->
                when (logConsumer.type!!) {
                    OutputFrame.OutputType.STDOUT -> logger.info("kafka:{}", logConsumer.utf8StringWithoutLineEnding)
                    OutputFrame.OutputType.STDERR -> logger.error("kafka:{}", logConsumer.utf8StringWithoutLineEnding)
                    OutputFrame.OutputType.END -> logger.info("kafka:end:{}", logConsumer.utf8StringWithoutLineEnding)
                }
            }
    }
}