package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.VehicleResponse;
import openerp.openerpresourceserver.service.VehicleService;

@RestController
@RequestMapping("/vehicles")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class VehicleController {

    private VehicleService vehicleService;

    @Secured("ROLE_WMS_DELIVERY_MANAGER")
    @GetMapping
    public List<VehicleResponse> getVehicles() {
        return vehicleService.getAllVehicles();
    }
    
    @Secured("ROLE_WMS_DELIVERY_MANAGER")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(vehicle);
    }
}

