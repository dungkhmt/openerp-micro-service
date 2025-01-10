package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.service.DeliveryTripService;

@RestController
@RequestMapping("/delivery-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryMangerController {

	private DeliveryTripService deliveryTripService;

	@GetMapping("/delivery-trips")
	public ResponseEntity<?> getDeliveryTrips(
	        @RequestParam(defaultValue = "CREATED") String status,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size) {
	    try {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<DeliveryTripProjection> deliveryTrips = deliveryTripService.getFilteredDeliveryTrips(status, pageable);
	        return ResponseEntity.ok(deliveryTrips);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while fetching delivery trips: " + e.getMessage());
	    }
	}


	@GetMapping("/delivery-trips/paths/{deliveryTripId}")
	public ResponseEntity<List<DeliveryTripPathProjection>> getDeliveryTripPaths(@PathVariable String deliveryTripId) {
		List<DeliveryTripPathProjection> paths = deliveryTripService.getDeliveryTripPaths(deliveryTripId);
		return ResponseEntity.ok(paths);
	}

}
