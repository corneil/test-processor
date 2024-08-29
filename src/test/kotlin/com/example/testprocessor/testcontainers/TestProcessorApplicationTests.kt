package com.example.testprocessor.testcontainers

import com.example.testprocessor.TestInput
import com.example.testprocessor.TestOutput
import com.example.testprocessor.TestProcessorApplication
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

val logger = LoggerFactory.getLogger(TestProcessorApplicationTests::class.java)!!

@SpringBootTest(
    classes = [TestListenerConfiguration::class, TestcontainersConfiguration::class, TestProcessorApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = [
        "com.example.testprocessor.addition=Soap",
        "spring.application.name=test-processor",
        "spring.cloud.function.definition=testProcessor",
        "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
        "spring.cloud.stream.function.bindings.testProcessor-out-0=output",
        "spring.cloud.stream.bindings.input.destination=input-queue",
        "spring.cloud.stream.bindings.output.destination=output-queue"
    ]
)
@Testcontainers
class TestProcessorApplicationTests {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    lateinit var listenerContainer: KafkaListenerContainer

    @Test
    fun contextLoads() {
    }

    @Test
    fun testProcessorBinding() {
        // given
        val mapper = jacksonObjectMapper()
        val input = TestInput("Joe", 1.0)
        // when
        Awaitility.await("send message to input-queue")
            .timeout(Duration.ofSeconds(10))
            .untilAsserted {
                val message = mapper.writeValueAsString(input)
                logger.info("sending:{}", message)
                val sendResult = kafkaTemplate.send("input-queue", message)
                val result = sendResult.get()
                logger.info("sent:{}", result)
                assertThat(result).isNotNull
                assertThat(result.recordMetadata).isNotNull
                assertThat(result.recordMetadata.topic()).isEqualTo("input-queue")
            }
        // wait for data from output-queue
        Awaitility.await("message from output-queue")
            .timeout(Duration.ofSeconds(10))
            .untilAsserted {
                // when
                assertThat(listenerContainer.messages).isNotEmpty
                val outputMessage = listenerContainer.messages.poll()

                logger.info("received:{}", outputMessage)
                // then
                assertThat(outputMessage).isNotNull
                // when
                val output = mapper.readValue<TestOutput>(outputMessage!!.value())
                logger.info("output = {}", output)
                // then
                assertThat(output).isNull()
                assertThat(output.surname).isEqualTo("Joe Soap")
            }
    }
}

@Configuration
class TestListenerConfiguration {
    @Bean
    fun kafkaListener() = KafkaListenerContainer()
}