package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.ShipmentRes;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shipment")
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping("/create")
    public ResponseEntity<?> createShipment (@RequestBody ShipmentModel shipmentModel) {
        ShipmentModel shipmentModelCreate = shipmentService.createShipment(shipmentModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModelCreate));
    }

    @PostMapping("/")
    public ResponseEntity<?> filterShipment (@RequestBody ShipmentFilterRequestDTO requestDTO) {
        ShipmentRes shipmentModels = shipmentService.filterShipment(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModels));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<?> getShipmentByShipmentId (@PathVariable String uid) {
        ShipmentModel shipmentModel = shipmentService.getShipmentByUid(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModel));
    }

    @PutMapping("/update/{uid}")
    public ResponseEntity<?> updateShipment(@PathVariable String uid, @RequestBody ShipmentModel shipmentModel) {
        ShipmentModel shipmentModelUpdate = shipmentService.updateShipment(uid, shipmentModel);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModelUpdate));
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<?> deleteShipment(@PathVariable String uid) {
        ShipmentModel shipmentModelDelete = shipmentService.deleteShipment(uid);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModelDelete));
    }
}
