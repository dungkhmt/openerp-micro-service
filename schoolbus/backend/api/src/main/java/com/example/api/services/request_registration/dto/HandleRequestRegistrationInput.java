package com.example.api.services.request_registration.dto;

import com.example.shared.enumeration.RequestRegistrationStatus;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandleRequestRegistrationInput {
    private List<Long> requestIds;
    private RequestRegistrationStatus status;
    private Long pickupPointId;
    private String address;
    private Double latitude;
    private Double longitude;
    private String note;
}
