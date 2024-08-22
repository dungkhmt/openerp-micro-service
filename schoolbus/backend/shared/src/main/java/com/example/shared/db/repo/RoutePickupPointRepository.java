package com.example.shared.db.repo;

import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.RoutePickupPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutePickupPointRepository extends JpaRepository<RoutePickupPoint, Long> {
    void deleteAllByPickupPoint(PickupPoint pickupPoint);
}
