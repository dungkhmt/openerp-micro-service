package com.example.shared.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
            .configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .findAndRegisterModules();

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMapper.class);

    public static <O> String writeValueAsString(O o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    public static <T> T convertJsonToObject(String jsonStr, final Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T convertJsonToObject(String jsonStr, final TypeReference<T> reference) {
        try {
            return mapper.readValue(jsonStr, reference);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<T> convertJsonToListObject(String jsonStr) {
        try {
            return mapper.readValue(jsonStr, new TypeReference<List<T>>() {
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new LinkedList<>();
        }
    }
}