package openerp.openerpresourceserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.entity.projection.DeliveryTripGeneralProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryTripProjection;
import openerp.openerpresourceserver.entity.projection.TodayDeliveryTripProjection;
import openerp.openerpresourceserver.model.request.DeliveryTripRequest;
import openerp.openerpresourceserver.service.DeliveryTripService;

@RestController
@RequestMapping("/delivery-trips")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripController {

	private DeliveryTripService deliveryTripService;

	@GetMapping
	public ResponseEntity<?> getDeliveryTrips(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdatedStamp").descending());
			Page<DeliveryTripProjection> deliveryTrips = deliveryTripService.getFilteredDeliveryTrips(status, pageable);
			return ResponseEntity.ok(deliveryTrips);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching delivery trips: " + e.getMessage());
		}
	}

	@GetMapping("/by-shipment")
	public ResponseEntity<Page<DeliveryTrip>> getDeliveryTrips(@RequestParam int page, @RequestParam int size,
			@RequestParam String shipmentId) {

		Page<DeliveryTrip> trips = deliveryTripService.getDeliveryTripsByShipmentId(page, size, shipmentId);
		return ResponseEntity.ok(trips);
	}

	@GetMapping("/today")
	public ResponseEntity<Page<TodayDeliveryTripProjection>> getTodayDeliveryTrips(
			@RequestParam String deliveryPersonId, @RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdStamp").descending());
		Page<TodayDeliveryTripProjection> trips = deliveryTripService.getTodayDeliveryTrips(deliveryPersonId, status,
				pageable);
		return ResponseEntity.ok(trips);
	}

	@GetMapping("/{deliveryTripId}/general-info")
	public ResponseEntity<DeliveryTripGeneralProjection> getDeliveryTripDetail(@PathVariable String deliveryTripId) {
		DeliveryTripGeneralProjection tripDetail = deliveryTripService.getDeliveryTripById(deliveryTripId);
		return ResponseEntity.ok(tripDetail);
	}

	@PostMapping
	public ResponseEntity<?> createDeliveryTrip(@RequestBody DeliveryTripRequest payload) {
		try {
			DeliveryTrip trip = deliveryTripService.createDeliveryTrip(payload);
			return ResponseEntity.ok(trip);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating trip");
		}

	}

	@PostMapping("/{deliveryTripId}/cancel")
	public ResponseEntity<String> cancelDeliveryTrip(@PathVariable String deliveryTripId) {
		boolean cancelled = deliveryTripService.cancelDeliveryTrip(deliveryTripId);
		if (cancelled) {
			return ResponseEntity.ok("Delivery trip cancelled successfully.");
		} else {
			return ResponseEntity.badRequest().body("Delivery trip can only be cancelled if it is in CREATED status.");
		}
	}

}
