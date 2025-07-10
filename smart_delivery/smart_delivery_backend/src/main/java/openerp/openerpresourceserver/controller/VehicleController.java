package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.dto.VehicleDto;
import openerp.openerpresourceserver.entity.Shipper;
import openerp.openerpresourceserver.repository.VehicleRepository;
import openerp.openerpresourceserver.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/smdeli/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'DRIVER_MANAGER')")
    @GetMapping("/getAll/{hubId}")
    public List<VehicleDto> getAllVehicleByHubId(@PathVariable UUID hubId) {
        List<VehicleDto> vehicles = vehicleService.getAllVehicleByHubId(hubId);
        return vehicles;
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER','ROUTE_MANAGER', 'DRIVER_MANAGER')")
    @GetMapping("/getAll")
    public List<VehicleDto> getAllVehicle() {
        List<VehicleDto> vehicles = vehicleService.getAll();
        return vehicles;
    }

    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'ADMIN', 'ROUTE_MANAGER', 'DRIVER_MANAGER')")
    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable UUID vehicleId) {
        VehicleDto vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'HUB_MANAGER', 'DRIVER_MANAGER')")
    @PostMapping("/vehicle")
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        return ResponseEntity.ok(vehicleService.createOrUpdateVehicle(vehicleDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER_MANAGER')")
    @DeleteMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID vehicleId){
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'ADMIN', 'ROUTE_MANAGER', 'DRIVER_MANAGER')")
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<VehicleDto> getVehicleByTripId(@PathVariable UUID tripId) {
        VehicleDto vehicle = vehicleService.getVehicleByTripId(tripId);
        return ResponseEntity.ok(vehicle);
    }
    @PreAuthorize("hasAnyRole('HUB_STAFF', 'HUB_MANAGER', 'ADMIN', 'ROUTE_MANAGER', 'DRIVER_MANAGER')")
    @GetMapping("/trip")
    public ResponseEntity<List<VehicleDto>> getVehicleByTripIds(@RequestBody List<UUID> tripIds) {
        List<VehicleDto> vehicles = vehicleService.getVehicleByTripIds(tripIds);
        return ResponseEntity.ok(vehicles);
    }

}
