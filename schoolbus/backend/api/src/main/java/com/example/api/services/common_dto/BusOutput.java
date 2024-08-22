package com.example.api.services.common_dto;

import com.example.shared.db.entities.Bus;
import com.example.shared.enumeration.BusStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusOutput {
    private Long id;
    private String numberPlate;
    private Integer seatNumber;
    private Long driverId;
    private Long driverMateId;
    private BusStatus status;
    private Double currentLatitude;
    private Double currentLongitude;

    private Instant createdAt;
    private Instant updatedAt;

    public static BusOutput fromEntity(Bus entity) {
        return BusOutput.builder()
            .id(entity.getId())
            .numberPlate(entity.getNumberPlate())
            .seatNumber(entity.getSeatNumber())
            .driverId(entity.getDriverId())
            .driverMateId(entity.getDriverMateId())
            .status(entity.getStatus())
            .currentLatitude(entity.getCurrentLatitude())
            .currentLongitude(entity.getCurrentLongitude())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
