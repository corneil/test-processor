package com.example.testprocessor.testcontainers.rabbit;

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
import org.testcontainers.containers.RabbitMQContainer;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RabbitTestListenerConfiguration.class, TestprocessorApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "com.example.testprocessor.addition=Soap",
                "spring.application.name=test-processor",
                "spring.cloud.function.definition=testProcessor",
                "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
                "spring.cloud.stream.function.bindings.testProcessor-out-0=output",
                "spring.cloud.stream.bindings.input.destination=input-queue",
                "spring.cloud.stream.bindings.input.group=test-consumer",
                "spring.cloud.stream.bindings.output.destination=output-queue",
                "spring.cloud.stream.bindings.output.group=test-consumer",
                "spring.cloud.stream.bindings.output.producer.requiredGroups=test-consumer",
                "spring.cloud.stream.default.binder=rabbit",
                "logging.level.org.springframework.amqp.rabbit=debug",
                "logging.level.root=error",
                "logging.level.org.apache=error",
                "logging.level.rabbitmq=debug",
                "logging.level.com.example=debug",
        }
)
@Tag("integration")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public abstract class RabbitTestProcessorTestBase {
    protected static final Logger logger = LoggerFactory.getLogger(RabbitTestProcessorTestBase.class);
    private static final Logger rabbitMQLogger = LoggerFactory.getLogger("rabbitmq");
    @Autowired
    protected RabbitMessagingTemplate rabbitTemplate;

    @Autowired
    protected RabbitListenerContainer listenerContainer;

    protected static RabbitMQContainer createRabbitMQContainer() {
        return new RabbitMQContainer()
                .withExposedPorts(5672, 15672)
                .withLogConsumer((logMessage) -> {
                    switch (logMessage.getType()) {
                        case STDERR -> rabbitMQLogger.error(logMessage.getUtf8StringWithoutLineEnding());
                        case STDOUT -> rabbitMQLogger.info(logMessage.getUtf8StringWithoutLineEnding());
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
        rabbitTemplate.send("input-queue.test-consumer", MessageBuilder.withPayload(message.getBytes()).build());
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
                    TestOutput output = mapper.readValue(outputMessage.getBody(), TestOutput.class);
                    logger.info("output = {}", output);
                    // then
                    assertThat(output).isNotNull();
                    assertThat(output.getFullName()).isEqualTo("Joe Soap");
                });
    }

}

