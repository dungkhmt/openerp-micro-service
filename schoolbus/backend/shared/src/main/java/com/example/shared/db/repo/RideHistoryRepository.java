package com.example.shared.db.repo;

import com.example.shared.db.entities.RideHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideHistoryRepository extends JpaRepository<RideHistory, Long> {
    List<RideHistory> findByRideId(Long rideId);
}
