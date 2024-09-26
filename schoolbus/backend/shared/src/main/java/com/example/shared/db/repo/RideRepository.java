package com.example.shared.db.repo;

import com.example.shared.db.entities.Ride;
import com.example.shared.enumeration.RideStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByBusIdAndStatus(Long busId, RideStatus status);

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.bus.id = :busId
        AND r.status = :status
        AND r.isToSchool = :isToSchool
        AND DATE(r.startAt) = DATE(:date)
    """)
    List<Ride> findByManipulateRide(
        @Param("busId") Long busId,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("date") Instant date);

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.bus.id = :busId
        AND r.status != :status
        AND r.isToSchool = :isToSchool
        AND DATE(r.startAt) = DATE(:date)
    """)
    List<Ride> findByManipulateRideNotInStatus(
        @Param("busId") Long busId,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("date") Instant date);

    @Query("""
            SELECT r
            FROM Ride r
            JOIN RidePickupPoint rpp ON r.id = rpp.ride.id
            WHERE rpp.pickupPoint.id = :pickupPointId
    """)
    List<Ride> findByPickupPointId(Long pickupPointId);

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.bus.id = :busId
        AND r.status = :status
        AND DATE(r.startAt) = DATE(:startAt)
    """)
    List<Ride> findByBusIdAndStatusAndStartAt(Long busId, RideStatus status, Instant startAt);

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.bus.id = :busId
        AND r.status != :status
        AND DATE(r.startAt) = DATE(:startAt)
    """)
    List<Ride> findByBusIdAndNotInStatusAndStartAt(Long busId, RideStatus status, Instant startAt);

    @Query("""
        SELECT r
        FROM Ride r
        LEFT JOIN StudentPickupPointHistory spph ON r.id = spph.rideId
        LEFT JOIN Student s ON spph.studentId = s.id
        LEFT JOIN Parent p ON s.parent.id = p.id
        WHERE (:isAllDate = TRUE OR DATE(r.startAt) = DATE(:startAt))
        AND (:riderId IS NULL OR r.id = :riderId)
        AND (:numberPlate IS NULL OR r.bus.numberPlate ILIKE %:numberPlate%)
        AND (:status IS NULL OR r.status = :status)
        AND (:isToSchool IS NULL OR r.isToSchool = :isToSchool)
        AND (:address IS NULL OR spph.address ILIKE %:address%)
        AND (:studentPhoneNumber IS NULL OR s.phoneNumber ILIKE %:studentPhoneNumber%)
        AND (:parentPhoneNumber IS NULL OR p.phoneNumber ILIKE %:parentPhoneNumber%)
    """)
    Page<Ride> searchHistory(
        @Param("startAt") Instant startAt,
        @Param("riderId") Integer riderId,
        @Param("numberPlate") String numberPlate,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("address") String address,
        @Param("studentPhoneNumber") String studentPhoneNumber,
        @Param("parentPhoneNumber") String parentPhoneNumber,
        @Param("isAllDate") Boolean isAllDate,
        Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Ride r
        WHERE (:isAllDate = TRUE OR DATE(r.startAt) = DATE(:startAt))
        AND (:riderId IS NULL OR r.id = :riderId)
        AND (:numberPlate IS NULL OR r.bus.numberPlate ILIKE %:numberPlate%)
        AND (:status IS NULL OR r.status = :status)
        AND (:isToSchool IS NULL OR r.isToSchool = :isToSchool)
    """)
    Page<Ride> searchHistory(
        @Param("startAt") Instant startAt,
        @Param("riderId") Integer riderId,
        @Param("numberPlate") String numberPlate,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("isAllDate") Boolean isAllDate,
        Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Ride r
        LEFT JOIN StudentPickupPointHistory spph ON r.id = spph.rideId
        LEFT JOIN Student s ON spph.studentId = s.id
        LEFT JOIN Parent p ON s.parent.id = p.id
        LEFT JOIN RideHistory rh ON r.id = rh.rideId
        WHERE (:isAllDate = TRUE OR DATE(r.startAt) = DATE(:startAt))
        AND (:riderId IS NULL OR r.id = :riderId)
        AND (:numberPlate IS NULL OR r.bus.numberPlate ILIKE %:numberPlate%)
        AND (:status IS NULL OR r.status = :status)
        AND (:isToSchool IS NULL OR r.isToSchool = :isToSchool)
        AND (:address IS NULL OR spph.address ILIKE %:address%)
        AND (:studentPhoneNumber IS NULL OR s.phoneNumber ILIKE %:studentPhoneNumber%)
        AND (:parentPhoneNumber IS NULL OR p.phoneNumber ILIKE %:parentPhoneNumber%)
        AND (rh.driverId = :employeeId OR rh.driverMateId = :employeeId)
    """)
    Page<Ride> searchEmployeeHistory(
        @Param("startAt") Instant startAt,
        @Param("riderId") Integer riderId,
        @Param("numberPlate") String numberPlate,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("address") String address,
        @Param("studentPhoneNumber") String studentPhoneNumber,
        @Param("parentPhoneNumber") String parentPhoneNumber,
        @Param("isAllDate") Boolean isAllDate,
        @Param("employeeId") Long employeeId,
        Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Ride r
        LEFT JOIN RideHistory rh ON r.id = rh.rideId
        WHERE (:isAllDate = TRUE OR DATE(r.startAt) = DATE(:startAt))
        AND (:riderId IS NULL OR r.id = :riderId)
        AND (:numberPlate IS NULL OR r.bus.numberPlate ILIKE %:numberPlate%)
        AND (:status IS NULL OR r.status = :status)
        AND (:isToSchool IS NULL OR r.isToSchool = :isToSchool)
        AND (rh.driverId = :employeeId OR rh.driverMateId = :employeeId)
    """)
    Page<Ride> searchEmployeeHistory(
        @Param("startAt") Instant startAt,
        @Param("riderId") Integer riderId,
        @Param("numberPlate") String numberPlate,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("isAllDate") Boolean isAllDate,
        @Param("employeeId") Long employeeId,
        Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Ride r
        LEFT JOIN StudentPickupPointHistory spph ON r.id = spph.rideId
        WHERE (:isAllDate = TRUE OR DATE(r.startAt) = DATE(:startAt))
        AND (:riderId IS NULL OR r.id = :riderId)
        AND (:numberPlate IS NULL OR r.bus.numberPlate ILIKE %:numberPlate%)
        AND (:status IS NULL OR r.status = :status)
        AND (:isToSchool IS NULL OR r.isToSchool = :isToSchool)
        AND (:address IS NULL OR spph.address ILIKE %:address%)
        AND (:studentIds IS NULL OR spph.studentId IN :studentIds)
    """)
    Page<Ride> searchClientHistory(
        @Param("startAt") Instant startAt,
        @Param("riderId") Integer riderId,
        @Param("numberPlate") String numberPlate,
        @Param("status") RideStatus status,
        @Param("isToSchool") Boolean isToSchool,
        @Param("address") String address,
        @Param("isAllDate") Boolean isAllDate,
        @Param("studentIds") List<Long> studentIds,
        Pageable pageable
    );

    @Query("""
    select r from Ride r
    left join Bus b
    on r.bus.id = b.id
    where (b.driverId = :employeeId or b.driverMateId = :employeeId)
    and DATE(r.startAt) = DATE(:startAt)
    """)
    List<Ride> findEmployeeRides(
        @Param("employeeId") Long employeeId,
        @Param("startAt") Instant startAt
    );

    @Query(value = """
        SELECT r.*
            FROM tieptd_194185_ride r
            WHERE DATE(r.start_at) = :date
            AND r.is_to_school = :isToSchool
            AND r.bus_id not in (
                SELECT r2.bus_id
                FROM tieptd_194185_ride r2
                WHERE DATE(r2.start_at) = DATE(:date) + interval '1' day
            )
    """, nativeQuery = true)
    List<Ride> findAllRidesByDateStartAt(
        @Param("date") Instant date,
        @Param("isToSchool") Boolean isToSchool
    );
}
