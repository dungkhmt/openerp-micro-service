package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.dto.TruckFilterRequestDTO;
import openerp.containertransport.dto.TruckFilterRes;
import openerp.containertransport.dto.TruckModel;
import openerp.containertransport.entity.Truck;
import openerp.containertransport.service.TruckService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/")
    public ResponseEntity<?> filterTruck(@RequestBody TruckFilterRequestDTO truckFilterRequestDTO) {
        TruckFilterRes truckModels = truckService.filterTruck(truckFilterRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(truckModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTruckById(@PathVariable long id) {
        TruckModel truckModel = truckService.getTruckById(id);
        return ResponseEntity.status(HttpStatus.OK).body(truckModel);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTruck(@PathVariable long id, @RequestBody TruckModel truckModel) {
        TruckModel truckModelUpdate = truckService.updateTruck(truckModel, id);
        return ResponseEntity.status(HttpStatus.OK).body(truckModelUpdate);
    }
}
