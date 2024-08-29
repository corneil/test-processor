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
        return mapper.readValue<TestInput>(message.payload as ByteArray)
    }
}
