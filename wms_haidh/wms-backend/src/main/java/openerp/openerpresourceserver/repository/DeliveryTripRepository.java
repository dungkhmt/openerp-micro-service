package openerp.openerpresourceserver.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.projection.DeliveryTripGeneralProjection;
import openerp.openerpresourceserver.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.projection.TodayDeliveryTripProjection;

public interface DeliveryTripRepository extends JpaRepository<DeliveryTrip, String> {

	@Query("SELECT " + "dt.deliveryTripId AS deliveryTripId, " + "w.name AS warehouseName, "
			+ "dp.fullName AS deliveryPersonName, " + "dt.distance AS distance, "
			+ "dt.totalLocations AS totalLocations, " + "dt.status AS status " + "FROM DeliveryTrip dt "
			+ "JOIN DeliveryPerson dp ON dt.deliveryPersonId = dp.userLoginId "
			+ "JOIN Warehouse w ON dt.warehouseId = w.warehouseId " + "WHERE dt.status = :status")
	Page<DeliveryTripProjection> findFilteredDeliveryTrips(@Param("status") String status, Pageable pageable);

	@Query("SELECT d.deliveryTripId AS deliveryTripId, " + "w.name AS warehouseName, " + "d.distance AS distance, "
			+ "d.totalLocations AS totalLocations, " + "d.status AS status " + "FROM DeliveryTrip d "
			+ "JOIN Shipment s ON d.shipmentId = s.shipmentId " + "JOIN Warehouse w ON d.warehouseId = w.warehouseId "
			+ "WHERE d.deliveryPersonId = :deliveryPersonId " + "AND DATE(s.expectedDeliveryStamp) = CURRENT_DATE "
			+ "AND d.status = :status")
	Page<TodayDeliveryTripProjection> findTodayTripsByDeliveryPerson(@Param("deliveryPersonId") String deliveryPersonId,
			@Param("status") String status, Pageable pageable);

	@Query("""
			    SELECT
			        dt.distance AS distance,
			        dt.totalWeight AS totalWeight,
			        dt.totalLocations AS totalLocations,
			        dt.status AS status,
			        dt.description AS description,
			        s.expectedDeliveryStamp AS expectedDeliveryStamp,
			        w.name AS warehouseName,
			        dp.fullName AS deliveryPersonName,
			        CONCAT(v.licensePlate, ' - ', v.description) AS vehicleName
			    FROM DeliveryTrip dt
			    JOIN Shipment s ON dt.shipmentId = s.shipmentId
			    JOIN Warehouse w ON dt.warehouseId = w.warehouseId
			    JOIN DeliveryPerson dp ON dt.deliveryPersonId = dp.userLoginId
			    JOIN Vehicle v ON dt.vehicleId = v.vehicleId
			    WHERE dt.deliveryTripId = :deliveryTripId
			""")
	Optional<DeliveryTripGeneralProjection> findDeliveryTripById(@Param("deliveryTripId") String deliveryTripId);

	Page<DeliveryTrip> findByShipmentId(String shipmentId, Pageable pageable);

}
