package com.example.api.services.common_dto;

import com.example.shared.db.entities.RidePickupPoint;
import com.example.shared.enumeration.RidePickupPointStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RidePickupPointOutput {
    private Long id;
    private Long rideId;
    private Long pickupPointId;
    private Integer orderIndex;
    private RidePickupPointStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    public static RidePickupPointOutput fromEntity(RidePickupPoint entity) {
        return RidePickupPointOutput.builder()
            .id(entity.getId())
            .rideId(entity.getRide().getId())
            .pickupPointId(entity.getPickupPoint().getId())
            .orderIndex(entity.getOrderIndex())
            .status(entity.getStatus())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
