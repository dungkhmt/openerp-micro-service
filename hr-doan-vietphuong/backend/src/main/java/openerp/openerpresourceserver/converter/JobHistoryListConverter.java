package openerp.openerpresourceserver.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import openerp.openerpresourceserver.entity.JobHistory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter
public class JobHistoryListConverter implements AttributeConverter<List<JobHistory>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<JobHistory> attribute) {
        objectMapper.registerModule(new JavaTimeModule());
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Error serializing List<JobHistory> to JSON", ex);
        }
    }

    @Override
    public List<JobHistory> convertToEntityAttribute(String dbData) {
        objectMapper.registerModule(new JavaTimeModule());
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, JobHistory.class));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to List<JobHistory>", e);
        }
    }
}