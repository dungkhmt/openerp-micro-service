package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.projection.DeliveryTripPathProjection;
import openerp.openerpresourceserver.service.DeliveryTripPathService;

@RestController
@RequestMapping("/delivery-trip-paths")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripPathController {

	private DeliveryTripPathService deliveryTripPathService;
	
	@GetMapping
	public ResponseEntity<List<DeliveryTripPathProjection>> getDeliveryTripPaths(@RequestParam String deliveryTripId) {
		List<DeliveryTripPathProjection> paths = deliveryTripPathService.getDeliveryTripPaths(deliveryTripId);
		return ResponseEntity.ok(paths);
	}

}
