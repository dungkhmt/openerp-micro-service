package com.example.api.controllers.employee.dto;

import com.example.api.services.ride_pickup_point.dto.UpdateRidePickupPointEmployeeInput;
import com.example.shared.enumeration.RidePickupPointStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateRidePickupPointRequest {
    private Long rideId;
    private Long pickupPointId;
    private RidePickupPointStatus status;

    public UpdateRidePickupPointEmployeeInput toInput() {
        return UpdateRidePickupPointEmployeeInput.builder()
                .rideId(rideId)
                .pickupPointId(pickupPointId)
                .status(status)
                .build();
    }
}
