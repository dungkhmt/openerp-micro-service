package com.example.api.services.pickup_point.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddPickupPointInput {
    private String address;
    private Double latitude;
    private Double longitude;
}
