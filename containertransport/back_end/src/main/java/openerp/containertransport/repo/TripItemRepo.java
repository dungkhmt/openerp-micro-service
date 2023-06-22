package openerp.containertransport.repo;

import openerp.containertransport.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TripItemRepo extends JpaRepository<TripItem, Long> {
    @Query(value = "SELECT * FROM container_transport_trip_item WHERE trip_id = :tripId",
            nativeQuery = true)
    List<TripItem> findByTripId(String tripId);
    TripItem findByUid(String uid);
}
