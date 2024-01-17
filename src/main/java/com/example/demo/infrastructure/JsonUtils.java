package com.example.demo.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;


public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .registerModule(new JsonNullableModule())
            .registerModule(new JavaTimeModule())
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> objectClass) throws JsonProcessingException {
        return objectMapper.readValue(json, objectClass);
    }
}
