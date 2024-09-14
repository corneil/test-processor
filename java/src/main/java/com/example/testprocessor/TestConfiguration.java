package com.example.testprocessor;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.example.testprocessor")
public class TestConfiguration {
    /**
     * Will be added to name to make fullName
     */
    private String addition = "N/A";

    public String getAddition() {
        return addition;
    }

    public void setAddition(String addition) {
        this.addition = addition;
    }
}
