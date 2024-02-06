package com.magic.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mzk
 */
public class JacksonUtils {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.createObjectMapper();
    public static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    public static String writeValueAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOGGER.error("writeValueAsString 发生异常",e);
            throw new RuntimeException();
        }
    }


    public static <T> T readValue(String content, Class<T> valueType) {
        try {
            return objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            LOGGER.error("readValue 发生异常",e);
            throw new RuntimeException();
        }
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            LOGGER.error("readValue 发生异常",e);
            throw new RuntimeException();
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return objectMapper.convertValue(fromValue, toValueTypeRef);
    }

    public static JsonNode readTree(Object fromValue) {
        try {
            return objectMapper.readTree(writeValueAsString(fromValue));
        } catch (JsonProcessingException e) {
            LOGGER.error("readTree 发生异常",e);
            throw new RuntimeException();
        }
    }


    public static ObjectMapper createObjectMapper() {
        return ObjectMapperFactory.createObjectMapper();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
