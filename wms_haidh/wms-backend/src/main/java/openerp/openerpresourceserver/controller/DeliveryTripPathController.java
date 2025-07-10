package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.dto.response.RoutePathResponse;
import openerp.openerpresourceserver.service.DeliveryTripPathService;

@RestController
@RequestMapping("/delivery-trip-paths")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripPathController {

	private DeliveryTripPathService deliveryTripPathService;
	
	@Secured({"ROLE_WMS_DELIVERY_PERSON","ROLE_WMS_DELIVERY_MANAGER"})
	@GetMapping
	public ResponseEntity<?> getDeliveryTripPaths(@RequestParam String deliveryTripId) {
	    try {
	        RoutePathResponse paths = deliveryTripPathService.getDeliveryTripPaths(deliveryTripId);
	        return ResponseEntity.ok(paths);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	@Secured({"ROLE_WMS_DELIVERY_PERSON","ROLE_WMS_DELIVERY_MANAGER"})
	@GetMapping("/waypoints")
	public ResponseEntity<?> getWaypoints(@RequestParam String deliveryTripId) {
	    try {
	        List<CoordinateDTO> waypoints = deliveryTripPathService.getWaypoints(deliveryTripId);
	        return ResponseEntity.ok(waypoints);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}


}
