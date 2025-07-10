package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.TripOrder;
import openerp.openerpresourceserver.entity.enumentity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TripOrderRepository extends JpaRepository<TripOrder, UUID> {

    /**
     * Find all TripOrder entities for a specific trip
     */
    List<TripOrder> findByTripIdOrderBySequenceNumber(UUID tripId);

    /**
     * Find all TripOrder entities for a specific order
     */
    List<TripOrder> findByOrderId(UUID orderId);

    /**
     * Find TripOrder by trip ID and order ID
     */
    TripOrder findByTripIdAndOrderId(UUID tripId, UUID orderId);

    /**
     * Find all orders in a trip with a specific status
     */
    List<TripOrder> findByTripIdAndStatus(UUID tripId, String status);

    /**
     * Count the number of orders in a trip
     */
    @Query("SELECT COUNT(to) FROM TripOrder to WHERE to.tripId = :tripId")
    Long countOrdersByTrip(@Param("tripId") UUID tripId);

    /**
     * Delete all TripOrder entities for a specific trip
     */
    void deleteByTripId(UUID tripId);

    /**
     * Delete all TripOrder entities for a specific order
     */
    void deleteByOrderId(UUID orderId);

    List<TripOrder> findByTripId(UUID id);

    List<TripOrder> findByTripIdAndDeliveredIsTrue(UUID tripId);

    List<TripOrder> findAllByTripId(UUID tripId);

    List<TripOrder> findAllByTripIdAndIsPickupIsFalse(UUID tripId);

    TripOrder findTopByOrderIdOrderByCreatedAtDesc(UUID orderId);

    TripOrder findByOrderIdAndStatus(UUID orderId, String status);

    @Query("select to from TripOrder to join Order o on to.orderId = o.id where to.tripId = :id and  to.orderId = o.id and to.status = :delivering and o.finalHubId = :hubId")
    List<TripOrder> findByTripIdAndStatusOrderByCreatedAtDescAndHubId(UUID id, UUID hubId, String delivering);
}