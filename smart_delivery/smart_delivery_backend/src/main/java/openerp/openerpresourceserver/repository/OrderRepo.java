package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.OrderResponseDto;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.OrderItem;
import openerp.openerpresourceserver.entity.enumentity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID>, OrderRepositoryCustom {
    @Query("SELECT o FROM Order o JOIN Sender s on o.senderId = s.senderId JOIN FETCH Recipient r on r.recipientId = o.recipientId WHERE o.id = :orderId")
    Optional<Order> findByIdWithSenderAndRecipient(@Param("orderId") UUID orderId);


    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.orderId = :orderId")
    Long countNumberOfItems(@Param("orderId") UUID orderId);
    List<Order> findByStatusAndFinalHubId(OrderStatus orderStatus, UUID hubId);

    List<Order> findByOriginHubIdOrderByCreatedAtDesc(UUID hubId);

    @Query("SELECT o FROM Order o join TripOrder to ON o.id = to.orderId where to.tripId = :tripId")
    List<Order> findByTripId(@Param("tripId") UUID tripId);

    List<Order> findAllByFinalHubIdAndStatus(UUID hubId, OrderStatus orderStatus);

    List<Order> findAllByOriginHubIdAndStatus(UUID hubId, OrderStatus orderStatus);

    @Query("select o from Order o where o.id in :orderIds and o.status = :orderStatus")
    List<Order> findAllByIdAndStatus(@Param("orderIds") List<UUID> orderIds,
                                     @Param("orderStatus") OrderStatus orderStatus);
    List<Order> findBySenderId(UUID id);

    List<Order> findByCreatedBy(String username);

    /**
     * Find orders by hub and status for InOrder functionality
     */
    @Query("SELECT o FROM Order o WHERE o.originHubId = :hubId AND o.status = :status")
    List<Order> findByOriginHubIdAndStatus(@Param("hubId") UUID hubId, @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.finalHubId = :hubId AND o.status = :status")
    List<Order> findByFinalHubIdAndStatus(@Param("hubId") UUID hubId, @Param("status") OrderStatus status);

    /**
     * Get orders collected by collectors (for InOrder tab 1)
     */
    @Query("SELECT o FROM Order o WHERE o.originHubId = :hubId AND o.status = 'COLLECTED_COLLECTOR'")
    List<Order> findCollectedCollectorOrdersByHub(@Param("hubId") UUID hubId);

    /**
     * Get orders delivered to hub by drivers (for InOrder tab 2)
     */
    @Query("SELECT o FROM Order o WHERE o.finalHubId = :hubId AND o.status = 'DELIVERED'")
    List<Order> findDeliveredDriverOrdersByHub(@Param("hubId") UUID hubId);

    /**
     * Get orders with failed delivery attempts (for InOrder tab 3)
     * This includes orders returned to hub after failed delivery
     */
    @Query("SELECT o FROM Order o " +
            "JOIN AssignOrderShipper aos ON o.id = aos.orderId " +
            "JOIN Shipper s ON aos.shipperId = s.id " +
            "WHERE s.hubId = :hubId " +
            "AND aos.status IN ('FAILED_ATTEMPT', 'RETURNED_TO_HUB')")
    List<Order> findFailedDeliveryOrdersByHub(@Param("hubId") UUID hubId);

    /**
     * Get suggested orders for trip assignment (for OutOrder driver tab)
     */
    @Query("SELECT o FROM Order o " +
            "WHERE o.originHubId = :originHubId " +
            "AND o.status = :status " +
            "AND o.finalHubId IN :destinationHubIds " +
            "ORDER BY o.createdAt ASC")
    List<Order> findSuggestedOrdersForTrip(
            @Param("originHubId") UUID originHubId,
            @Param("status") OrderStatus status,
            @Param("destinationHubIds") List<UUID> destinationHubIds);

    /**
     * Get orders ready for shipper delivery at hub (for OutOrder shipper tab)
     */
    @Query("SELECT o FROM Order o WHERE o.finalHubId = :hubId AND o.status = 'DELIVERED'")
    List<Order> findOrdersReadyForShipperDelivery(@Param("hubId") UUID hubId);

    /**
     * Find orders by username (creator)
     */
    /**
     * Find orders by hub and today
     */
    @Query("SELECT o FROM Order o WHERE o.originHubId = :hubId AND DATE(o.createdAt) = CURRENT_DATE")
    List<Order> findByHubAndToday(@Param("hubId") UUID hubId);
}
