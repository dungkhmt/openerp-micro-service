package com.example.api.controllers.admin.dto;

import com.example.api.services.pickup_point.dto.AddPickupPointInput;
import lombok.Data;

@Data
public class AddPickupPointRequest {
    private String address;
    private Double latitude;
    private Double longitude;

    public AddPickupPointInput toInput() {
        return AddPickupPointInput.builder()
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
