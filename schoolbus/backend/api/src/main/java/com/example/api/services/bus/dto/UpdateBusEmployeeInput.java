package com.example.api.services.bus.dto;

import com.example.shared.enumeration.BusStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateBusEmployeeInput {
    private String numberPlate;
    private BusStatus status;
}
