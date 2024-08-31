package com.example.testprocessor.mock;

import java.io.IOException;

import com.example.testprocessor.TestInput;
import com.example.testprocessor.TestOutput;
import com.example.testprocessor.TestprocessorApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@EnableTestBinder
@SpringBootTest(
    classes = TestprocessorApplication.class,
    properties = {
        "com.example.testprocessor.addition=Soap",
        "spring.application.name=test-processor",
        "spring.cloud.function.definition=testProcessor",
        "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
        "spring.cloud.stream.function.bindings.testProcessor-out-0=output"
    }
)
@Tag("test")
public class MockBinderTests {
    private final static Logger logger = LoggerFactory.getLogger(MockBinderTests.class);

    @Autowired
    InputDestination input;

    @Autowired
    OutputDestination output;

    @Test
    void testProcessorBinding() throws IOException {
        // given
        var inputData = new TestInput("Joe", 1.0);
        var message = MessageBuilder.withPayload(inputData).build();
        input.send(message);
        logger.info("sent {} as {}", inputData, message);
        // when
        var outputMessage = output.receive(1000, "output");
        logger.info("received = {}", outputMessage);
        // then
        assertThat(outputMessage).isNotNull();
        // when
        var mapper = new ObjectMapper();
        var output = mapper.readValue(outputMessage.getPayload(), TestOutput.class);
        logger.info("output = {}", output);
        // then
        assertThat(output.getFullName()).isEqualTo("Joe Soap");
    }

}
