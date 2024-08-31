package com.example.testprocessor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(TestConfiguration.class)
public class TestProcessorConfiguration {
    @Bean
    public TestProcessor testProcessor(TestConfiguration configuration) {
        return new TestProcessor(configuration);
    }

    @Bean
    TestOutputConverter testOutputConverter() {
        return new TestOutputConverter();
    }

    @Bean
    TestInputConverter testInputConverter() {
        return new TestInputConverter();
    }
}
