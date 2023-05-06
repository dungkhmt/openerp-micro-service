package openerp.containertransport.controller;

import lombok.RequiredArgsConstructor;
import openerp.containertransport.constants.MetaData;
import openerp.containertransport.dto.ShipmentFilterRequestDTO;
import openerp.containertransport.dto.ShipmentModel;
import openerp.containertransport.dto.metaData.MetaDTO;
import openerp.containertransport.dto.metaData.ResponseMetaData;
import openerp.containertransport.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
        List<ShipmentModel> shipmentModels = shipmentService.filterShipment(requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), shipmentModels));
    }
}
