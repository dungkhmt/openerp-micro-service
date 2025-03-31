package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripItemRepository extends JpaRepository<TripItem, UUID> {
    List<TripItem> findByTripId(UUID tripId);

    List<TripItem> findAllByTripId(UUID tripId);

    TripItem findByOrderItemId(UUID orderItemId);

    @Query("SELECT ti FROM TripItem ti WHERE ti.tripId = :tripId AND ti.status = :status")
    List<TripItem> findByTripIdAndStatus(@Param("tripId") UUID tripId, @Param("status") String status);

    @Query("SELECT ti FROM TripItem ti WHERE ti.confirmedOutBy IS NOT NULL AND ti.tripId = :tripId")
    List<TripItem> findConfirmedOutByTripId(@Param("tripId") UUID tripId);

    @Query("SELECT ti FROM TripItem ti WHERE ti.confirmedInBy IS NOT NULL AND ti.tripId = :tripId")
    List<TripItem> findConfirmedInByTripId(@Param("tripId") UUID tripId);

    @Query("SELECT COUNT(ti) FROM TripItem ti WHERE ti.tripId = :tripId AND ti.status = 'PICKED_UP'")
    int countPickedUpItemsByTripId(@Param("tripId") UUID tripId);

    @Query("SELECT COUNT(ti) FROM TripItem ti WHERE ti.tripId = :tripId AND ti.status = 'DELIVERED'")
    int countDeliveredItemsByTripId(@Param("tripId") UUID tripId);


    List<TripItem> findAllByTripIdAndIsPickupIsFalse(UUID tripId);

    List<TripItem> findAllByTripIdAndStatus(UUID tripId, String delivering);

    TripItem findTopByOrderItemIdOrderByCreatedAtDesc(UUID orderItemId);
}