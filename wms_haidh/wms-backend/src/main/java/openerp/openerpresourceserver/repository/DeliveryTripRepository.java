package openerp.openerpresourceserver.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import openerp.openerpresourceserver.dto.response.DeliveryTripGeneralResponse;
import openerp.openerpresourceserver.dto.response.DeliveryTripResponse;
import openerp.openerpresourceserver.dto.response.TodayDeliveryTripResponse;
import openerp.openerpresourceserver.entity.DeliveryTrip;

public interface DeliveryTripRepository extends JpaRepository<DeliveryTrip, String> {

	@Query("SELECT new openerp.openerpresourceserver.dto.response.DeliveryTripResponse(dt.deliveryTripId,"
			+ "w.name," + "dp.fullName," + "dt.distance," + "dt.totalLocations, " + "dt.status) "
			+ "FROM DeliveryTrip dt " + "JOIN DeliveryPerson dp ON dt.deliveryPersonId = dp.userLoginId "
			+ "JOIN Warehouse w ON dt.warehouseId = w.warehouseId " + "WHERE dt.status = :status")
	Page<DeliveryTripResponse> findFilteredDeliveryTrips(@Param("status") String status, Pageable pageable);

	@Query("SELECT new openerp.openerpresourceserver.dto.response.TodayDeliveryTripResponse( d.deliveryTripId, " + "w.name, " + "d.distance, "
			+ "d.totalLocations, " + "d.status) " + "FROM DeliveryTrip d "
			+ "JOIN Shipment s ON d.shipmentId = s.shipmentId " + "JOIN Warehouse w ON d.warehouseId = w.warehouseId "
			+ "WHERE d.deliveryPersonId = :deliveryPersonId " + "AND DATE(s.expectedDeliveryStamp) = CURRENT_DATE "
			+ "AND d.status = :status")
	Page<TodayDeliveryTripResponse> findTodayTripsByDeliveryPerson(@Param("deliveryPersonId") String deliveryPersonId,
			@Param("status") String status, Pageable pageable);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.DeliveryTripGeneralResponse(
			        dt.distance,
			        dt.totalWeight,
			        dt.totalLocations,
			        dt.status,
			        dt.description,
			        s.expectedDeliveryStamp,
			        w.name,
			        dp.fullName,
			        CONCAT(v.licensePlate, ' - ', v.description))
			    FROM DeliveryTrip dt
			    JOIN Shipment s ON dt.shipmentId = s.shipmentId
			    JOIN Warehouse w ON dt.warehouseId = w.warehouseId
			    JOIN DeliveryPerson dp ON dt.deliveryPersonId = dp.userLoginId
			    JOIN Vehicle v ON dt.vehicleId = v.vehicleId
			    WHERE dt.deliveryTripId = :deliveryTripId
			""")
	Optional<DeliveryTripGeneralResponse> findDeliveryTripById(@Param("deliveryTripId") String deliveryTripId);

	Page<DeliveryTrip> findByShipmentId(String shipmentId, Pageable pageable);

}
