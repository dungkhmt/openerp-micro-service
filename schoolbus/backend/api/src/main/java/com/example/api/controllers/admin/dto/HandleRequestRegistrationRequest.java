package com.example.api.controllers.admin.dto;

import com.example.api.services.request_registration.dto.HandleRequestRegistrationInput;
import com.example.shared.enumeration.RequestRegistrationStatus;
import java.util.List;
import lombok.Data;

@Data
public class HandleRequestRegistrationRequest {
    private List<Long> requestIds;
    private RequestRegistrationStatus status;
    private Long pickupPointId;
    private String address;
    private Double latitude;
    private Double longitude;
    private String note;

    public HandleRequestRegistrationInput toInput() {
        return HandleRequestRegistrationInput.builder()
            .requestIds(requestIds)
            .status(status)
            .pickupPointId(pickupPointId)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .note(note)
            .build();
    }
}
