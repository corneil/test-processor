package com.example.testprocessor.mock

import com.example.testprocessor.TestInput
import com.example.testprocessor.TestOutput
import com.example.testprocessor.TestProcessorApplication
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.EnableTestBinder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.integration.support.MessageBuilder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode

@EnableTestBinder
@SpringBootTest(
    classes = [TestProcessorApplication::class],
    properties = ["com.example.testprocessor.addition=Soap",
        "spring.application.name=test-processor",
        "spring.cloud.function.definition=testProcessor",
        "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
        "spring.cloud.stream.function.bindings.testProcessor-out-0=output"
    ]
)
@Tag("test")
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class MockBinderTests {
    companion object {
        val logger = LoggerFactory.getLogger(MockBinderTests::class.java)!!
    }

    @Autowired
    lateinit var input: InputDestination

    @Autowired
    lateinit var output: OutputDestination

    @Test
    fun testProcessorBinding() {

        // given
        val inputData = TestInput("Joe", 1.0)
        val message = MessageBuilder.withPayload(inputData).build()
        input.send(message)
        // when
        val outputMessage = output.receive(1000)
        logger.info("output = {}", outputMessage)
        // then
        assertThat(outputMessage).isNotNull
        // when
        val mapper = jacksonObjectMapper()
        val output = mapper.readValue<TestOutput>(outputMessage.payload)
        // then
        assertThat(output.fullName).isEqualTo("Joe Soap")
    }
}
