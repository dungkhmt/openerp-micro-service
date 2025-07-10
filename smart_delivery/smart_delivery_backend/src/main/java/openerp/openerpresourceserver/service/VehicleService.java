package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.dto.VehicleDto;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    List<VehicleDto> getAllVehicleByHubId(UUID hubId);
    List<VehicleDto> getAll();
    VehicleDto getVehicleById(UUID vehicleId);
    VehicleDto createOrUpdateVehicle(VehicleDto vehicleDto);
    void deleteVehicle(UUID vehicleId);

    VehicleDto getVehicleByTripId(UUID tripId);

    List<VehicleDto> getVehicleByTripIds(List<UUID> tripIds);
}
