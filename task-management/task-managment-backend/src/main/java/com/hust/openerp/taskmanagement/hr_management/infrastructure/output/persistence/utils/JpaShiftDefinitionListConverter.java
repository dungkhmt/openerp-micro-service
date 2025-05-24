package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster.ShiftDefinition;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JpaShiftDefinitionListConverter implements AttributeConverter<List<ShiftDefinition>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JpaShiftDefinitionListConverter.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ShiftDefinition> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null; // Hoặc "[]" nếu bạn muốn lưu mảng JSON rỗng
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting List<ShiftDefinition> to JSON string: {}", attribute, e);
            return null;
        }
    }

    @Override
    public List<ShiftDefinition> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "null".equalsIgnoreCase(dbData)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<ArrayList<ShiftDefinition>>() {});
        } catch (IOException e) {
            logger.error("Error converting JSON string to List<ShiftDefinition>: {}", dbData, e);
            return new ArrayList<>();
        }
    }
}

