package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.projection.VehicleProjection;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

	@Query("SELECT v.vehicleId AS vehicleId, CONCAT(v.licensePlate, ' - ', v.description) AS name, v.maxWeight AS maxWeight "
			+ "FROM Vehicle v WHERE v.status = 'AVAILABLE'")
	List<VehicleProjection> findAllAvailableVehicles();

	@Query("SELECT v.vehicleId AS vehicleId, " + "CONCAT(v.licensePlate, ' - ', v.description) AS name, "
			+ "v.maxWeight AS maxWeight " + "FROM Vehicle v " + "WHERE v.vehicleId = :vehicleId")
	VehicleProjection findVehicleProjectionById(@Param("vehicleId") UUID vehicleId);

}
