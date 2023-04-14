package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.service.TruckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/truck")
public class TruckController {
    private final TruckService truckService;
    @PostMapping("/create")
    public ResponseEntity<?> createTruck(@RequestBody TruckModel truckModel) {
        Truck truck = truckService.createTruck(truckModel);
        return ResponseEntity.status(HttpStatus.OK).body(truck);
    }
}
