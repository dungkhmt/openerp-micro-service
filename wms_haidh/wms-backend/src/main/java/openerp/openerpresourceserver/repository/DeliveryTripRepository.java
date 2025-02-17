package openerp.openerpresourceserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;

public interface DeliveryTripRepository extends JpaRepository<DeliveryTrip, String> {

    @Query("SELECT " +
           "dt.deliveryTripId AS deliveryTripId, " +
           "dp.fullName AS deliveryPersonName, " +
           "dt.distance AS distance, " +
           "dt.totalLocations AS totalLocations, " +
           "dt.status AS status, " +
           "dt.description AS description " +
           "FROM DeliveryTrip dt " +
           "JOIN DeliveryPerson dp ON dt.deliveryPersonId = dp.userLoginId " +
           "WHERE dt.isDeleted = false " +
           "AND dt.status = :status")
    Page<DeliveryTripProjection> findFilteredDeliveryTrips(@Param("status") String status, Pageable pageable);
}
