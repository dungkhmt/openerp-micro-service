package com.example.api.controllers.admin.dto;

import com.example.api.services.bus.dto.AddBusInput;
import com.example.shared.enumeration.BusStatus;
import lombok.Data;

@Data
public class AddBusRequest {
    private String numberPlate;
    private Integer seatNumber;
    private Long driverId;
    private Long driverMateId;
    private BusStatus status;

    public AddBusInput toInput() {
        return AddBusInput.builder()
                .numberPlate(numberPlate)
                .seatNumber(seatNumber)
                .driverId(driverId)
                .driverMateId(driverMateId)
                .status(status)
                .build();
    }
}
