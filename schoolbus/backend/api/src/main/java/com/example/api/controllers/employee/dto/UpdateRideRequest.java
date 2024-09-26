package com.example.api.controllers.employee.dto;

import com.example.api.services.ride.dto.UpdateRideEmployeeInput;
import com.example.shared.enumeration.RideStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRideRequest {
    private Long rideId;
    private RideStatus status;

    public UpdateRideEmployeeInput toInput() {
        return UpdateRideEmployeeInput.builder()
                .rideId(rideId)
                .status(status)
                .build();
    }
}
