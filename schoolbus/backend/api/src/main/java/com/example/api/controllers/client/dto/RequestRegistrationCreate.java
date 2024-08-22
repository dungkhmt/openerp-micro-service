package com.example.api.controllers.client.dto;

import com.example.api.services.request_registration.dto.CreateRequestInput;
import java.util.List;
import lombok.Data;

@Data
public class RequestRegistrationCreate {
    private List<Long> studentIds;
    private String address;
    private Double longitude;
    private Double latitude;

    public CreateRequestInput toInput() {
        return CreateRequestInput.builder()
            .studentIds(studentIds)
            .address(address)
            .longitude(longitude)
            .latitude(latitude)
            .build();
    }
}

