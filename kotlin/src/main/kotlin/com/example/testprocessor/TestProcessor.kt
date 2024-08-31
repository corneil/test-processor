package com.example.testprocessor

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.function.Function

val logger = LoggerFactory.getLogger(TestProcessor::class.java)

class TestProcessor(val config: TestConfiguration) : Function<TestInput, TestOutput> {

    override fun apply(input: TestInput): TestOutput {
        val output = TestOutput(input.name, input.value, input.name + " " + config.addition)
        logger.info("processing {} -> {}", input, output)
        return output
    }
}

@ConfigurationProperties(prefix = "com.example.testprocessor")
data class TestConfiguration(val addition: String = "N/A")

data class TestInput(val name: String, val value: Double)
data class TestOutput(val name: String, val value: Double, val fullName: String)

