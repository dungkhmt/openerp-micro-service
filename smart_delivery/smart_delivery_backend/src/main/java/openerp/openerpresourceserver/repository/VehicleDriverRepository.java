package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto;
import openerp.openerpresourceserver.entity.VehicleDriver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, UUID> {
    VehicleDriver findByVehicleId(UUID vehicleId);

    boolean existsByVehicleIdAndUnassignedAtIsNull(UUID vehicleId);

    boolean existsByDriverIdAndUnassignedAtIsNull(UUID driverId);

    VehicleDriver findByVehicleIdAndUnassignedAtIsNull(UUID vehicleId);

    VehicleDriver findByDriverIdAndUnassignedAtIsNull(UUID driverId);

    List<VehicleDriver> findByUnassignedAtIsNull();
    @Query("SELECT new openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto(vd.vehicleId, v.plateNumber, v.vehicleType, v.model, v.manufacturer, v.status, vd.driverId, d.name,d.code,d.phone) FROM VehicleDriver vd join Vehicle v on vd.vehicleId = v.vehicleId join Driver d on d.id = vd.driverId WHERE vd.unassignedAt IS NULL")
    List<DriverVehicleAssignmentDto> getAllDriverVehicleAssignments();
}
