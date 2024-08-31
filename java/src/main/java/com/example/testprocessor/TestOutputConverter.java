package com.example.testprocessor;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.AbstractMessageConverter;

public class TestOutputConverter extends AbstractMessageConverter {
    private ObjectMapper mapper =  new ObjectMapper();
    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.equals(TestOutput.class);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, Object conversionHint) {
        try {
            if (message.getPayload() instanceof String) {
                return mapper.readValue((String) message.getPayload(), TestOutput.class);
            } else if(message.getPayload() instanceof byte[]) {
                return mapper.readValue((byte[]) message.getPayload(), TestOutput.class);
            } else {
                throw new RuntimeException("Cannot process message of type:" + message.getPayload().getClass());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
