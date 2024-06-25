package com.example.api.services.ride_pickup_point;

import com.example.api.services.ride_pickup_point.dto.AddRidePickupPointInput;
import com.example.api.services.ride_pickup_point.dto.GetListRidePickupPointOutput;
import com.example.api.services.ride_pickup_point.dto.RidePickupPointFilterParam;
import com.example.api.services.ride_pickup_point.dto.UpdateRidePickupPointEmployeeInput;
import com.example.api.services.ride_pickup_point.dto.UpdateRidePickupPointInput;
import com.example.shared.db.entities.Account;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RidePickupPointService {
    Page<GetListRidePickupPointOutput> getListRidePickupPoint(
        RidePickupPointFilterParam filterParam,
        Pageable pageable
    );

    void addRidePickupPoint(AddRidePickupPointInput addRidePickupPointInput);

    void updateRidePickupPoint(UpdateRidePickupPointInput updateRidePickupPointInput);

    void deleteRidePickupPoints(List<Long> ridePickupPointIds);

    void updateRidePickupPointEmployee(UpdateRidePickupPointEmployeeInput input,
                                       Account account);
}
