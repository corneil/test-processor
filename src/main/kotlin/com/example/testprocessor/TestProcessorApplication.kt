package com.example.testprocessor


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(proxyBeanMethods = false)
@Import(TestProcessorConfiguration::class)
class TestProcessorApplication

fun main(args: Array<String>) {
    runApplication<TestProcessorApplication>(*args)
}

