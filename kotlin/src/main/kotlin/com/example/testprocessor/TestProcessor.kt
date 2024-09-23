package com.example.testprocessor

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.boot.context.properties.bind.Name
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
class TestConfiguration {
    /**
     * Will be added to name to make fullName
     */
    @setparam:DefaultValue("N/A")
    @setparam:Name("addition")
    var addition: String = "N/A"
}

data class TestInput(val name: String, val value: Double)
data class TestOutput(val name: String, val value: Double, val fullName: String)

@ConfigurationProperties(prefix = "com.example.testprocessor2")
class TestConfiguration2(
    /**
     * Will be added to name to make fullName
     */
    @DefaultValue("N/A")
    @Name("addition")
    val addition: String = "N/A"
)
