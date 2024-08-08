package com.example.api.services.common_dto;

import com.example.shared.db.entities.PickupPoint;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickupPointOutput {
    private Long id;
    private String address;
    private Double latitude;
    private Double longitude;
    private Instant createdAt;
    private Instant updatedAt;

    public static  PickupPointOutput fromEntity(PickupPoint entity) {
        return  PickupPointOutput.builder()
            .id(entity.getId())
            .address(entity.getAddress())
            .latitude(entity.getLatitude())
            .longitude(entity.getLongitude())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
