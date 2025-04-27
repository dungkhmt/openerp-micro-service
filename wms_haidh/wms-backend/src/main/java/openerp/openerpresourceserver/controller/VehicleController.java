package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.projection.VehicleProjection;
import openerp.openerpresourceserver.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class VehicleController {

    private VehicleService vehicleService;

    @GetMapping
    public List<VehicleProjection> getVehicles() {
        return vehicleService.getAllVehicles();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VehicleProjection> getVehicleById(@PathVariable UUID id) {
        VehicleProjection vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }
}

