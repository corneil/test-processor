package com.example.testprocessor


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.messaging.Message
import org.springframework.messaging.converter.AbstractMessageConverter
import org.springframework.util.MimeTypeUtils
import java.util.function.Function

@SpringBootApplication(proxyBeanMethods = false)
@Import(TestProcessorConfiguration::class)
class TestProcessorApplication

fun main(args: Array<String>) {
    runApplication<TestProcessorApplication>(*args)
}

@Configuration
@EnableConfigurationProperties(TestConfiguration::class)
class TestProcessorConfiguration {
    companion object {
        val logger = LoggerFactory.getLogger(TestProcessorConfiguration::class.java)
    }
    @Bean("testProcessor")
    fun testProcessor(testConfiguration: TestConfiguration): Function<TestInput, TestOutput> {
        logger.info("initializing test processor with: $testConfiguration")
        return TestProcessorClass(testConfiguration)
    }
    @Bean
    fun testInputConverter() = TestInputConverter()

    @Bean
    fun testOutputConverter() = TestOutputConverter()
}

@ConfigurationProperties(prefix = "com.example.testprocessor")
data class TestConfiguration(val addition: String)

data class TestInput(val name: String, val value:Double)
data class TestOutput(val name: String, val value: Double, val surname: String)

class TestProcessorClass(val config: TestConfiguration) : Function<TestInput, TestOutput> {
    companion object {
        val logger = LoggerFactory.getLogger(TestProcessorConfiguration::class.java)
    }
    override fun apply(input: TestInput): TestOutput {
        val output = TestOutput(input.name, input.value, input.name + " " + config.addition)
        logger.info("processing ${input} -> ${output}")
        return output
    }
}

class TestInputConverter : AbstractMessageConverter(MimeTypeUtils.APPLICATION_JSON) {
    val mapper = jacksonObjectMapper()
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == TestInput::class.java
    }

    override fun convertFromInternal(message: Message<*>, targetClass: Class<*>, conversionHint: Any?): Any? {
        return mapper.readValue<TestInput>(message.payload as ByteArray)
    }
}

class TestOutputConverter : AbstractMessageConverter(MimeTypeUtils.APPLICATION_JSON) {
    val mapper = jacksonObjectMapper()
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == TestOutput::class.java
    }
    override fun convertFromInternal(message: Message<*>, targetClass: Class<*>, conversionHint: Any?): Any? {
        return mapper.readValue<TestOutput>(message.payload as ByteArray)
    }
}