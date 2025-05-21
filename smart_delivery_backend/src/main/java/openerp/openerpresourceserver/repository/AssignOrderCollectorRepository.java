package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.AssignOrderCollectorDTO;
import openerp.openerpresourceserver.entity.AssignOrderCollector;
import openerp.openerpresourceserver.entity.enumentity.CollectorAssignmentStatus;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface AssignOrderCollectorRepository extends JpaRepository<AssignOrderCollector, UUID> {
    @Query("select a " +
             "from AssignOrderCollector a " +
             "JOIN Order o ON a.orderId = o.id " +
             "JOIN Hub h ON o.originHubId = h.hubId "
             )
    List<AssignOrderCollector> findByHubIdAndCreatedAtBetween(
            @Param("hubId") UUID hubId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    @Query("SELECT new openerp.openerpresourceserver.dto.AssignOrderCollectorDTO(" +
            "a.id, a.orderId, a.sequenceNumber, s.address, s.name, s.phone, s.longitude, s.latitude, o.status, o.createdAt, a.status, " +
            "(SELECT COUNT(oi) FROM OrderItem oi WHERE oi.orderId = a.orderId) " + // Thêm subquery đếm số lượng items
            ") " +
            "FROM AssignOrderCollector a " +
            "JOIN Order o ON a.orderId = o.id " +
            "JOIN Sender s ON o.senderId = s.senderId " +
            "WHERE a.collectorId = :collectorId " +
            "AND a.createdAt BETWEEN :startDate AND :endDate")
    List<AssignOrderCollectorDTO> findByCollectorIdAndCreatedAtBetween1(
            @Param("collectorId") UUID collectorId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);
    List<AssignOrderCollector> findByCollectorIdAndCreatedAtBetween(
            @Param("collectorId") UUID collectorId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);
    AssignOrderCollector findByOrderIdAndStatus(UUID orderId, CollectorAssignmentStatus status);
}
