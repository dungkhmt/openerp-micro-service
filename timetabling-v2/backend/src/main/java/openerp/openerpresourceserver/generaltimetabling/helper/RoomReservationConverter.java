package openerp.openerpresourceserver.generaltimetabling.helper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;

@Converter(autoApply = true)
public class RoomReservationConverter implements AttributeConverter<List<RoomReservation>, String> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RoomReservation> roomReservations) {
        try {
            return objectMapper.writeValueAsString(roomReservations);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting RoomReservations to JSON", e);
        }
    }

    @Override
    public List<RoomReservation> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<RoomReservation>>(){});
        } catch (IOException e) {
            throw new IllegalArgumentException("Error converting JSON to RoomReservations", e);
        }
    }
}