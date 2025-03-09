package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER')")
    @GetMapping("/getAll/{hubId}")
    public List<VehicleDto> getAllVehicleByHubId(@PathVariable UUID hubId) {
        List<VehicleDto> vehicles = vehicleService.getAllVehicleByHubId(hubId);
        return vehicles;
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER','ROUTE_MANAGER')")
    @GetMapping("/getAll")
    public List<VehicleDto> getAllVehicle() {
        List<VehicleDto> vehicles = vehicleService.getAll();
        return vehicles;
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'ADMIN', 'ROUTE_MANAGER')")
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable UUID vehicleId) {
        VehicleDto vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }
}
