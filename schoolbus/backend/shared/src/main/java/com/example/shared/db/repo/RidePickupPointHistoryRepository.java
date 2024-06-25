package com.example.shared.db.repo;

import com.example.shared.db.entities.RidePickupPointHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RidePickupPointHistoryRepository extends
    JpaRepository<RidePickupPointHistory, Long> {
    void deleteAllByRideId(Long rideId);

    List<RidePickupPointHistory> findByRideId(Long rideId);
}
