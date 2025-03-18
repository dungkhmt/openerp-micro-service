package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.dto.DriverVehicleAssignmentDto;

import java.util.List;
import java.util.UUID;

public interface DriverVehicleAssignmentService {

    void assignDriverToVehicle(UUID driverId, UUID vehicleId);
    void unassignDriverFromVehicle(UUID vehicleId);
    VehicleDto getVehicleForDriver(UUID driverId);
    List<VehicleDto> getUnassignedVehicles();
    List<DriverVehicleAssignmentDto> getAllDriverVehicleAssignments();
}
