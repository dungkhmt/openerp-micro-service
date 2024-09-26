package com.example.api.services.bus.dto;

import com.example.shared.enumeration.BusStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBusInput {
    private Long id;
    private String numberPlate;
    private Integer seatNumber;
    private Long driverId;
    private Long driverMateId;
    private BusStatus status;
}
