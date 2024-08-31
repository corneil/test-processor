package com.example.testprocessor;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProcessor implements Function<TestInput, TestOutput> {
    private final static Logger logger = LoggerFactory.getLogger(TestProcessor.class);
    final private TestConfiguration configuration;

    public TestProcessor(TestConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public TestOutput apply(TestInput input) {
        return new TestOutput(input.getName(), input.getValue(), input.getName() + " " + configuration.getAddition());
    }
}
