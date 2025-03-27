package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TripItemRepository extends JpaRepository<TripItem, UUID> {
    List<TripItem> findByTripId(UUID tripId);

    List<TripItem> findAllByTripId(UUID tripId);

    TripItem findByOrderItemId(UUID orderItemId);
}
