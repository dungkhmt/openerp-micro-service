package com.example.api.services.ride_pickup_point.dto;

import com.example.shared.db.dto.GetListRidePickupPointDTO;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.RidePickupPoint;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetListRidePickupPointOutput {
    private Ride ride;
    private PickupPoint pickupPoint;
    private RidePickupPoint ridePickupPoint;

    public static GetListRidePickupPointOutput fromDto(GetListRidePickupPointDTO dto) {
        return GetListRidePickupPointOutput.builder()
            .ride(dto.getRide())
            .pickupPoint(dto.getPickupPoint())
            .ridePickupPoint(dto.getRidePickupPoint())
            .build();
    }
}
