package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.TodayAssignmentShipperDto;
import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssignOrderShipperRepository extends JpaRepository<AssignOrderShipper, UUID> {

    @Query("SELECT a FROM AssignOrderShipper a WHERE a.shipperId = :shipperId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<AssignOrderShipper> findByShipperIdAndCreatedAtBetween(
            @Param("shipperId") UUID shipperId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    List<AssignOrderShipper> findByShipperId(UUID shipperId);

    List<AssignOrderShipper> findByOrderId(UUID orderId);

    AssignOrderShipper findByOrderIdAndStatus(UUID orderId, ShipperAssignmentStatus status);

    @Query("SELECT a FROM AssignOrderShipper a JOIN Order o ON a.orderId = o.id WHERE o.finalHubId = :hubId AND a.status IN :statuses")
    List<AssignOrderShipper> findByHubIdAndStatusIn(
            @Param("hubId") UUID hubId,
            @Param("statuses") List<ShipperAssignmentStatus> statuses
    );

    @Query("SELECT COUNT(a) FROM AssignOrderShipper a WHERE a.shipperId = :shipperId AND a.status = :status")
    Long countByShipperIdAndStatus(
            @Param("shipperId") UUID shipperId,
            @Param("status") ShipperAssignmentStatus status
    );


    /**
     * Get shipper assignments for today by hub with aggregated counts
     */
    @Query("SELECT new openerp.openerpresourceserver.dto.TodayAssignmentShipperDto(" +
            "aos.shipperId, " +
            "aos.shipperName, " +
            "COUNT(aos.orderId), " +
            "SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
            "s.phone, " +
            "CASE " +
            "  WHEN COUNT(aos.orderId) = SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END) THEN 'COMPLETED' " +
            "  WHEN SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END) > 0 THEN 'IN_PROGRESS' " +
            "  ELSE 'ASSIGNED' " +
            "END) " +
            "FROM AssignOrderShipper aos " +
            "JOIN Shipper s ON aos.shipperId = s.id " +
            "WHERE s.hubId = :hubId " +
            "AND DATE(aos.createdAt) = :today " +
            "GROUP BY aos.shipperId, aos.shipperName, s.phone")
    List<TodayAssignmentShipperDto> getShipperAssignmentsTodayByHub(
            @Param("hubId") UUID hubId,
            @Param("today") LocalDate today);

    /**
     * Alternative native query implementation for better performance
     */
    @Query(value = """
        SELECT 
            aos.shipper_id as shipperId,
            aos.shipper_name as shipperName,
            COUNT(aos.order_id) as numOfOrders,
            COALESCE(SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) as numOfCompleted,
            s.phone as shipperPhone,
            CASE 
                WHEN COUNT(aos.order_id) = COALESCE(SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) THEN 'COMPLETED'
                WHEN COALESCE(SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) > 0 THEN 'IN_PROGRESS'
                ELSE 'ASSIGNED'
            END as status
        FROM smartdelivery_assign_order_shipper aos
        JOIN smartdelivery_shipper s ON aos.shipper_id = s.id
        WHERE s.hub_id = :hubId
        AND DATE(aos.created_at) = :today
        GROUP BY aos.shipper_id, aos.shipper_name, s.phone
        ORDER BY aos.shipper_name
        """, nativeQuery = true)
    List<Object[]> getShipperAssignmentsTodayByHubNative(
            @Param("hubId") UUID hubId,
            @Param("today") LocalDate today);

    /**
     * Get shipper assignments that are pending pickup (assigned but not completed)
     */
    @Query("SELECT new openerp.openerpresourceserver.dto.TodayAssignmentShipperDto(" +
            "aos.shipperId, " +
            "aos.shipperName, " +
            "COUNT(aos.orderId), " +
            "SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END), " +
            "s.phone, " +
            "'WAITING_PICKUP') " +
            "FROM AssignOrderShipper aos " +
            "JOIN Shipper s ON aos.shipperId = s.id " +
            "WHERE s.hubId = :hubId " +
            "AND DATE(aos.createdAt) = :today " +
            "AND aos.status IN ('ASSIGNED', 'PICKED_UP') " +
            "GROUP BY aos.shipperId, aos.shipperName, s.phone " +
            "HAVING COUNT(aos.orderId) > SUM(CASE WHEN aos.status = 'COMPLETED' THEN 1 ELSE 0 END)")
    List<TodayAssignmentShipperDto> getShipperPickupRequestsByHub(
            @Param("hubId") UUID hubId,
            @Param("today") LocalDate today);

    /**
     * Find assignments by shipper ID and status
     */
    List<AssignOrderShipper> findByShipperIdAndStatus(UUID shipperId, ShipperAssignmentStatus status);

    /**
     * Find assignments by shipper ID for today
     */
    @Query("SELECT aos FROM AssignOrderShipper aos " +
            "WHERE aos.shipperId = :shipperId " +
            "AND DATE(aos.createdAt) = :today")
    List<AssignOrderShipper> findByShipperIdAndToday(
            @Param("shipperId") UUID shipperId,
            @Param("today") LocalDate today);

    /**
     * Get count of pending orders for a shipper
     */
    @Query("SELECT COUNT(aos) FROM AssignOrderShipper aos " +
            "WHERE aos.shipperId = :shipperId " +
            "AND aos.status = 'ASSIGNED'")
    Long countPendingOrdersByShipper(@Param("shipperId") UUID shipperId);

    /**
     * Get assignments by hub and status
     */
    @Query("SELECT aos FROM AssignOrderShipper aos " +
            "JOIN Shipper s ON aos.shipperId = s.id " +
            "WHERE s.hubId = :hubId " +
            "AND aos.status = :status")
    List<AssignOrderShipper> findByHubIdAndStatus(
            @Param("hubId") UUID hubId,
            @Param("status") ShipperAssignmentStatus status);
}