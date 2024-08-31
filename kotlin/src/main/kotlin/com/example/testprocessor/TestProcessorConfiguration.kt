package com.example.testprocessor

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function

@Configuration
@EnableConfigurationProperties(TestConfiguration::class)
class TestProcessorConfiguration {
    companion object {
        val logger = LoggerFactory.getLogger(TestProcessorConfiguration::class.java)
    }

    @Bean("testProcessor")
    fun testProcessor(testConfiguration: TestConfiguration): Function<TestInput, TestOutput> {
        logger.info("initializing test processor with: $testConfiguration")
        return TestProcessor(testConfiguration)
    }

    @Bean
    fun testInputConverter() = TestInputConverter()

    @Bean
    fun testOutputConverter() = TestOutputConverter()
}