package openerp.containertransport.repo;

import openerp.containertransport.entity.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TripItemRepo extends JpaRepository<TripItem, Long> {
    @Query(value = "SELECT * FROM container_transport_trip_item WHERE trip_id = :tripId",
            nativeQuery = true)
    List<TripItem> findByTripId(String tripId);

    TripItem findByUid(String uid);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM container_transport_trip_item WHERE trip_id = :tripsUid",
            nativeQuery = true)
    void deleteByTripUid(String tripsUid);

    @Query(value = "DELETE FROM container_transport_trip_item WHERE uid = :uid RETURNING *",
            nativeQuery = true)
    TripItem deleteByUid(String uid);
}
