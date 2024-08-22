package com.example.api.controllers.admin.dto;

import com.example.api.services.bus.dto.UpdateBusInput;
import com.example.shared.enumeration.BusStatus;
import lombok.Data;

@Data
public class UpdateBusRequest {
    private Long id;
    private String numberPlate;
    private Integer seatNumber;
    private Long driverId;
    private Long driverMateId;
    private BusStatus status;

    public UpdateBusInput toInput() {
        return UpdateBusInput.builder()
                .id(id)
                .numberPlate(numberPlate)
                .seatNumber(seatNumber)
                .driverId(driverId)
                .driverMateId(driverMateId)
                .status(status)
                .build();
    }
}
