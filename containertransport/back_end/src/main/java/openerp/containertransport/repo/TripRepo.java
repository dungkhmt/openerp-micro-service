package openerp.containertransport.repo;

import openerp.containertransport.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepo extends JpaRepository<Trip, Long> {
    Trip findByUid(String uid);

    @Query(value = "DELETE FROM container_transport_trip WHERE uid = :uid RETURNING *",
            nativeQuery = true)
    Trip deleteTripByUid(String uid);

    @Query(value = "SELECT * FROM container_transport_trip WHERE shipment_id = :shipmentId",
            nativeQuery = true)
    List<Trip> getTripByShipmentId(String shipmentId);
}
