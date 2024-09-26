package com.example.shared.db.repo;

import com.example.shared.db.dto.GetListRidePickupPointDTO;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.Ride;
import com.example.shared.db.entities.RidePickupPoint;
import com.example.shared.enumeration.RidePickupPointStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RidePickupPointRepository extends JpaRepository<RidePickupPoint, Long> {

    @Query("""
        SELECT r as ride, pp as pickupPoint, rpp as ridePickupPoint
            FROM RidePickupPoint rpp
            LEFT JOIN Ride r ON rpp.ride.id = r.id
            LEFT JOIN PickupPoint pp ON rpp.pickupPoint.id = pp.id
            WHERE
                (:rideId IS NULL OR r.id = :rideId)
                AND (:pickupPointId IS NULL OR pp.id = :pickupPointId)
                AND (:status IS NULL OR rpp.status = :status)
    """)
    Page<GetListRidePickupPointDTO> getListRidePickupPoint(
        @Param("rideId") Long rideId,
        @Param("pickupPointId") Long pickupPointId,
        @Param("status") RidePickupPointStatus status,
        Pageable pageable
    );

    void deleteAllByPickupPoint(PickupPoint pickupPoint);

    boolean existsByRideIdAndPickupPointId(Long rideId, Long pickupPointId);

    // deleteAllByRide
    void deleteAllByRide(Ride ride);

    // deleteAllByRideId
    void deleteAllByRideId(Long rideId);

    List<RidePickupPoint> findByRideId(Long rideId);

    @Query("""
        SELECT pp
        FROM PickupPoint pp
        JOIN RidePickupPoint rpp ON pp.id = rpp.pickupPoint.id
        WHERE rpp.ride.id = :rideId
    """)
    List<PickupPoint> findPickupPointsByRideId(Long rideId);

    // findByRideIdAndPickupPointId
    Optional<RidePickupPoint> findByRideIdAndPickupPointId(Long rideId, Long pickupPointId);

    @Query("""
        SELECT rpp.pickupPoint.id
        FROM RidePickupPoint rpp
        WHERE rpp.ride.id = :rideId
    """)
    List<Long> findPickupPointIdsByRideId(Long rideId);
}
