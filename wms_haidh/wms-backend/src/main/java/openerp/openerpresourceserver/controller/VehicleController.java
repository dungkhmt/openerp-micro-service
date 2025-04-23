package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
}

