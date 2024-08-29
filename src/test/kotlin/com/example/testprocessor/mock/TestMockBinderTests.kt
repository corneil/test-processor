package com.example.testprocessor.mock

import com.example.testprocessor.TestInput
import com.example.testprocessor.TestOutput
import com.example.testprocessor.TestProcessorConfiguration
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.EnableTestBinder
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.integration.support.MessageBuilder

import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@EnableTestBinder
@SpringBootTest(classes = [TestProcessorTestApplication::class],
    properties = ["com.example.testprocessor.addition=Malan",
        "spring.application.name=test-processor",
        "spring.cloud.function.definition=testProcessor",
        "spring.cloud.stream.function.bindings.testProcessor-in-0=input",
        "spring.cloud.stream.function.bindings.testProcessor-out-0=output"
    ]
)
class TestMockBinderTests {
    companion object {
        val logger = LoggerFactory.getLogger(TestMockBinderTests::class.java)!!
    }

    @Autowired
    lateinit var input: InputDestination

    @Autowired
    lateinit var output: OutputDestination
    @Test
    fun testProcessorBinding() {

        // given
        val inputData = TestInput("Jacques", 1.0)
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
        assertThat(output.surname).isEqualTo("Jacques Malan")
    }
}



@SpringBootApplication(proxyBeanMethods = false)
@Import(TestProcessorConfiguration::class)
class TestProcessorTestApplication
fun main(args: Array<String>) {
    runApplication<TestProcessorTestApplication>(*args)
}
