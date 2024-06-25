package com.example.shared.db.dto;

import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.RidePickupPoint;

public interface GetListRidePickupPointDTO {
    Ride getRide();
    PickupPoint getPickupPoint();
    RidePickupPoint getRidePickupPoint();
}
