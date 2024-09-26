package com.example.api.services.ride;

import com.example.api.services.ride.dto.UpdateRideEmployeeInput;
import com.example.api.services.ride.dto.UpsertRideInput;
import com.example.api.services.ride.dto.UpdateRideInput;
import com.example.shared.db.entities.Account;

public interface RideService {
    void upsertRide(UpsertRideInput upsertRideInput);

    void updateRide(UpdateRideInput updateRideInput);

    void updateRideEmployee(UpdateRideEmployeeInput updateRideEmployeeInput, Account account);
}
