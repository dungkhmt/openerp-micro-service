package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Shipment;
import openerp.openerpresourceserver.model.request.ShipmentRequest;
import openerp.openerpresourceserver.service.ShipmentService;

@RestController
@RequestMapping("/shipments")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ShipmentController {

	private ShipmentService shipmentService;
	
	@GetMapping
	public ResponseEntity<?> getShipments(
	        @RequestParam(required = false) Integer page,
	        @RequestParam(required = false) Integer size,
	        @RequestParam(required = false) String search) {

	    if (page != null && size != null) {
	        Page<Shipment> shipments = shipmentService.getAllShipments(page, size, search);
	        return ResponseEntity.ok(shipments);
	    } else {
	        List<Shipment> shipments = shipmentService.getAllShipments();
	        return ResponseEntity.ok(shipments);
	    }
	}

	@PostMapping
    public ResponseEntity<Shipment> createShipment(@RequestBody ShipmentRequest request) {
        Shipment shipment = shipmentService.createShipment(request);
        return ResponseEntity.ok(shipment);
    }
	

}
