package com.example.testprocessor.testcontainers.kafka;

import java.time.Duration;

import com.example.testprocessor.TestInput;
import com.example.testprocessor.TestOutput;
import com.example.testprocessor.TestprocessorApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {KafkaTestListenerConfiguration.class, TestprocessorApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "com.example.testprocessor.addition=Soap",
                "spring.application.name=test-processor",
                "spring.cloud.function.definition=testProcessor",
                "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
                "spring.cloud.stream.function.bindings.testProcessor-out-0=output",
                "spring.cloud.stream.bindings.input.destination=input-queue",
                "spring.cloud.stream.bindings.output.destination=output-queue",
                "spring.cloud.stream.default.binder=kafka",
                "logging.level.root=error",
                "logging.level.org.apache=error",
                "logging.level.kafka=error",
                "logging.level.com.example=debug"
        }
)
@Tag("integration")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
abstract class KafkaTestProcessorTestBase {
    protected static final Logger logger = LoggerFactory.getLogger(KafkaTestProcessorTestBase.class);
    private static final Logger kafkaLogger = LoggerFactory.getLogger("kafka");
    @Autowired
    protected KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    protected KafkaListenerContainer listenerContainer;

    protected static KafkaContainer createKafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
                .withKraft()
                .withLogConsumer((logMessage) -> {
                    switch (logMessage.getType()) {
                        case STDERR -> kafkaLogger.error(logMessage.getUtf8StringWithoutLineEnding());
                        case STDOUT -> kafkaLogger.info(logMessage.getUtf8StringWithoutLineEnding());
                    }
                });
    }

    @Test
    void testProcessorBinding() throws JsonProcessingException {
        // given
        var mapper = new ObjectMapper();
        var input = new TestInput("Joe", 1.0);
        // when
        var message = mapper.writeValueAsString(input);
        logger.info("sending {} as {}", input, message);
        var sendResult = kafkaTemplate.send("input-queue", message.getBytes());
        // then
        Awaitility.await("send message to input-queue")
                .timeout(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(2))
                .pollDelay(Duration.ofSeconds(0))
                .untilAsserted(() -> {
                    var result = sendResult.get();
                    assertThat(result).isNotNull();
                    assertThat(result.getRecordMetadata()).isNotNull();
                    assertThat(result.getRecordMetadata().topic()).isEqualTo("input-queue");
                    logger.info("sent:{}", result);
                });
        // wait for data from output-queue
        Awaitility.await("message from output-queue")
                .timeout(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(2))
                .pollDelay(Duration.ofSeconds(0))
                .untilAsserted(() -> {
                    // when
                    assertThat(listenerContainer.hasMessages()).as("has messages in queue").isTrue();
                    var outputMessage = listenerContainer.fetchMessage();

                    logger.info("received:{}", outputMessage);
                    // then
                    assertThat(outputMessage).isNotNull();
                    // when
                    TestOutput output;
                    if (outputMessage.value() instanceof String) {
                        output = mapper.readValue((String) outputMessage.value(), TestOutput.class);
                    } else if (outputMessage.value() instanceof byte[]) {
                        output = mapper.readValue((byte[]) outputMessage.value(), TestOutput.class);
                    } else {
                        throw new RuntimeException("Cannot process message of type:" + outputMessage.value().getClass());
                    }
                    logger.info("output = {}", output);
                    // then
                    assertThat(output).isNotNull();
                    assertThat(output.getFullName()).isEqualTo("Joe Soap");
                });
    }

}

