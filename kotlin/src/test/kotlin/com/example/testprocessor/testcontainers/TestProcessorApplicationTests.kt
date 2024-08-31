package com.example.testprocessor.testcontainers

import com.example.testprocessor.TestInput
import com.example.testprocessor.TestOutput
import com.example.testprocessor.TestProcessorApplication
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.output.OutputFrame
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration


@SpringBootTest(
    classes = [TestListenerConfiguration::class, TestProcessorApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "com.example.testprocessor.addition=Soap",
        "spring.application.name=test-processor",
        "spring.cloud.function.definition=testProcessor",
        "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
        "spring.cloud.stream.function.bindings.testProcessor-out-0=output",
        "spring.cloud.stream.bindings.input.destination=input-queue",
        "spring.cloud.stream.bindings.output.destination=output-queue",
        "logging.level.root=error",
        "logging.level.org.apache=error",
        "logging.level.kafka=error",
        "logging.level.com.example=debug"
    ]
)
@Testcontainers
@Tag("integration")
class TestProcessorApplicationTests {
    companion object {
        val kafkaLogger = LoggerFactory.getLogger("kafka")!!
        @Container
        @JvmStatic
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
            .withLogConsumer { logConsumer ->
                when (logConsumer.type!!) {
                    OutputFrame.OutputType.STDOUT -> kafkaLogger.info(logConsumer.utf8StringWithoutLineEnding)
                    OutputFrame.OutputType.STDERR -> kafkaLogger.error(logConsumer.utf8StringWithoutLineEnding)
                    OutputFrame.OutputType.END -> kafkaLogger.info(logConsumer.utf8StringWithoutLineEnding)
                }
            }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
        }

        val logger = LoggerFactory.getLogger(TestProcessorApplicationTests::class.java)!!
    }

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, ByteArray>

    @Autowired
    lateinit var listenerContainer: KafkaListenerContainer

    @Test
    fun testProcessorBinding() {
        // given
        val mapper = jacksonObjectMapper()
        val input = TestInput("Joe", 1.0)
        // when
        val message = mapper.writeValueAsString(input)
        logger.info("sending:{}", message)
        val sendResult = kafkaTemplate.send("input-queue", message.toByteArray())
        // then
        Awaitility.await("send message to input-queue")
            .timeout(Duration.ofSeconds(15))
            .pollInterval(Duration.ofSeconds(2))
            .pollDelay(Duration.ofSeconds(0))
            .untilAsserted {
                val result = sendResult.get()
                assertThat(result).isNotNull
                assertThat(result.recordMetadata).isNotNull
                assertThat(result.recordMetadata.topic()).isEqualTo("input-queue")
                logger.info("sent:{}", result)
            }
        // wait for data from output-queue
        Awaitility.await("message from output-queue")
            .timeout(Duration.ofSeconds(15))
            .pollInterval(Duration.ofSeconds(2))
            .pollDelay(Duration.ofSeconds(0))
            .untilAsserted {
                // when
                assertThat(listenerContainer.hasMessages()).`as`("has messages in queue").isTrue()
                val outputMessage = listenerContainer.fetchMessage()

                logger.info("received:{}", outputMessage)
                // then
                assertThat(outputMessage).isNotNull
                // when
                val output = when (outputMessage.value()) {
                    is String -> mapper.readValue<TestOutput>(outputMessage.value() as String)
                    is ByteArray -> mapper.readValue<TestOutput>(outputMessage.value() as ByteArray)
                    else -> throw RuntimeException("Cannot process message of type:" + outputMessage.value()::class)
                }
                logger.info("output = {}", output)
                // then
                assertThat(output).isNotNull
                assertThat(output.fullName).isEqualTo("Joe Soap")
            }
    }
}

@TestConfiguration(proxyBeanMethods = false)
class TestListenerConfiguration {
    @Bean
    fun kafkaListener() = KafkaListenerContainer()
}
