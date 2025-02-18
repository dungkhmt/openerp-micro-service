package openerp.openerpresourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import openerp.openerpresourceserver.entity.DeliveryTripPath;
import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;

public interface DeliveryTripPathRepository extends JpaRepository<DeliveryTripPath, Integer> {

    @Query("SELECT " +
           "dtp.longitude AS lng, " +
           "dtp.latitude AS lat " +
           "FROM DeliveryTripPath dtp " +
           "WHERE dtp.deliveryTripId = :deliveryTripId")
    List<DeliveryTripPathProjection> findByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);
}
