package com.example.testprocessor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.messaging.Message
import org.springframework.messaging.converter.AbstractMessageConverter
import org.springframework.util.MimeTypeUtils

class TestInputConverter : AbstractMessageConverter(MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.TEXT_PLAIN) {
    val mapper = jacksonObjectMapper()
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == TestInput::class.java
    }

    override fun convertFromInternal(message: Message<*>, targetClass: Class<*>, conversionHint: Any?): Any? {
        return when (message.payload) {
            is String -> mapper.readValue<TestInput>(message.payload as String)
            is ByteArray -> mapper.readValue<TestInput>(message.payload as ByteArray)
            else -> throw RuntimeException("Cannot process message of type:" + message.payload::class)
        }
    }
}

class TestOutputConverter : AbstractMessageConverter(MimeTypeUtils.APPLICATION_JSON, MimeTypeUtils.TEXT_PLAIN) {
    val mapper = jacksonObjectMapper()
    override fun supports(clazz: Class<*>): Boolean {
        return clazz == TestOutput::class.java
    }

    override fun convertFromInternal(message: Message<*>, targetClass: Class<*>, conversionHint: Any?): Any? {
        return when (message.payload) {
            is String -> mapper.readValue<TestOutput>(message.payload as String)
            is ByteArray -> mapper.readValue<TestOutput>(message.payload as ByteArray)
            else -> throw RuntimeException("Cannot process message of type:" + message.payload::class)
        }
    }
}