package com.example.demo.infrastructure;

import com.example.demo.domain.language.LanguageModelReturn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

public class JsonUtils {
    public static String toJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.writeValueAsString(object);
    }
    public static <T> T fromJson(String json, Class<T> objectClass) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.readValue(json, objectClass);
    }
}
