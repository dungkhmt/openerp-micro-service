package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.dto.response.VehicleResponse;
import openerp.openerpresourceserver.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

	@Query(" SELECT new openerp.openerpresourceserver.dto.response.VehicleResponse( v.vehicleId, CONCAT(v.licensePlate, ' - ', v.description), v.maxWeight) "
			+ "FROM Vehicle v WHERE v.status = 'AVAILABLE'")
	List<VehicleResponse> findAllAvailableVehicles();

	@Query("SELECT new openerp.openerpresourceserver.dto.response.VehicleResponse( v.vehicleId, " + "CONCAT(v.licensePlate, ' - ', v.description), "
			+ "v.maxWeight) " + "FROM Vehicle v " + "WHERE v.vehicleId = :vehicleId")
	VehicleResponse findVehicleProjectionById(@Param("vehicleId") UUID vehicleId);

}
