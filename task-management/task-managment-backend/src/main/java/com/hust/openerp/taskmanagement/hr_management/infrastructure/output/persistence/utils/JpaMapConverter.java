package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class JpaMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JpaMapConverter.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null; // Hoặc "null" nếu bạn muốn lưu chuỗi "null" thay vì NULL của SQL
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting map to JSON string: {}", attribute, e);
            // Hoặc throw new RuntimeException("Error converting map to JSON string", e);
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "null".equalsIgnoreCase(dbData)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<HashMap<String, Object>>() {});
        } catch (IOException e) {
            logger.error("Error converting JSON string to map: {}", dbData, e);
            // Hoặc throw new RuntimeException("Error converting JSON string to map", e);
            return new HashMap<>();
        }
    }
}
