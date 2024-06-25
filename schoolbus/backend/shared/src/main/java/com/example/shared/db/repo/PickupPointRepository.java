package com.example.shared.db.repo;

import com.example.shared.db.dto.GetListPickupPointDTO;
import com.example.shared.db.dto.GetStudentPickupPointDTO;
import com.example.shared.db.entities.PickupPoint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PickupPointRepository extends JpaRepository<PickupPoint, Long> {

    @Query("""
    SELECT p AS pickupPoint, 
           s AS students, 
           r AS rides 
    FROM PickupPoint p 
    LEFT JOIN FETCH StudentPickupPoint sp ON p.id = sp.pickupPoint.id 
    LEFT JOIN FETCH Student s ON s.id = sp.student.id 
    LEFT JOIN FETCH RidePickupPoint rp ON p.id = rp.pickupPoint.id 
    LEFT JOIN FETCH Ride r ON r.id = rp.ride.id 
    WHERE (:address is NULL or p.address ILIKE %:address%)
    """)
    Page<GetListPickupPointDTO> getListPickupPoint(
        @Param("address") String address,
        Pageable pageable
    );

    @Query("""
    SELECT p
    FROM PickupPoint p
    WHERE (:address is NULL or p.address ILIKE %:address%)
    """)
    Page<PickupPoint> getPagePickupPoint(
        @Param("address") String address,
        Pageable pageable
    );

    @Query("""
    SELECT p AS pickupPoint, 
           s AS student
    FROM Student s
    LEFT JOIN FETCH StudentPickupPoint sp ON s.id = sp.student.id
    LEFT JOIN FETCH PickupPoint p ON p.id = sp.pickupPoint.id
    WHERE s.parent.id = :parentId
    """)
    Page<GetStudentPickupPointDTO> getListStudentPickupPointByParentId(@Param("parentId") Long parentId, Pageable pageable);

    boolean existsByAddress(String address);

    @Query("""
    SELECT p
    FROM PickupPoint p
    JOIN RidePickupPoint rp ON p.id = rp.pickupPoint.id
    WHERE rp.ride.id = :rideId
    ORDER BY rp.orderIndex ASC
    """)
    List<PickupPoint> findByRideId(Long rideId);

    @Query("""
    SELECT p
    FROM PickupPoint p
    JOIN StudentPickupPoint spp ON p.id = spp.pickupPoint.id
    WHERE spp.student.id = :studentId
    """)
    Optional<PickupPoint> findByStudentId(Long studentId);

//    @Modifying
//    @Transactional
//    @Query("""
//    DELETE FROM PickupPoint p
//    WHERE NOT EXISTS (
//        SELECT 1
//        FROM StudentPickupPoint spp
//        WHERE spp.pickupPoint.id = p.id
//    )
//    """)
//    void deletePickupPointWithNoStudent();
}
