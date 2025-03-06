package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.AssignOrderShipper;
import openerp.openerpresourceserver.entity.enumentity.ShipperAssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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
}