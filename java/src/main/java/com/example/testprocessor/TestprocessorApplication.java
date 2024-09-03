package com.example.testprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(TestProcessorConfiguration.class)
public class TestprocessorApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestprocessorApplication.class, args);
	}

}
