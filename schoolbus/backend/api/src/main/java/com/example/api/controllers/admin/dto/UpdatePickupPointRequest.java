package com.example.api.controllers.admin.dto;

import com.example.api.services.pickup_point.dto.UpdatePickupPointInput;
import lombok.Data;

@Data
public class UpdatePickupPointRequest {
    private Long id;
    private String address;
    private Double latitude;
    private Double longitude;

    public UpdatePickupPointInput toInput() {
        return UpdatePickupPointInput.builder()
            .id(id)
            .address(address)
            .latitude(latitude)
            .longitude(longitude)
            .build();
    }
}
