package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.response.VehicleResponse;
import openerp.openerpresourceserver.entity.Vehicle;
import openerp.openerpresourceserver.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAllAvailableVehicles();
    }
    
    public void updateVehicleStatus(UUID vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Xe không tồn tại"));

        vehicle.setStatus(status);  
        vehicleRepository.save(vehicle); 
    }
    
    public VehicleResponse getVehicleById(UUID vehicleId) {
    	 return vehicleRepository.findVehicleProjectionById(vehicleId);
    }
}

