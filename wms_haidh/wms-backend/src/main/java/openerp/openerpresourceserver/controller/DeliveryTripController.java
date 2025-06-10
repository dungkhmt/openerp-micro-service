package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.DeliveryTripCreateRequest;
import openerp.openerpresourceserver.dto.response.DeliveryTripGeneralResponse;
import openerp.openerpresourceserver.dto.response.DeliveryTripResponse;
import openerp.openerpresourceserver.dto.response.TodayDeliveryTripResponse;
import openerp.openerpresourceserver.entity.DeliveryTrip;
import openerp.openerpresourceserver.service.DeliveryTripService;

@RestController
@RequestMapping("/delivery-trips")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripController {

	private DeliveryTripService deliveryTripService;

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping
	public ResponseEntity<?> getDeliveryTrips(@RequestParam(defaultValue = "CREATED") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdatedStamp").descending());
			Page<DeliveryTripResponse> deliveryTrips = deliveryTripService.getFilteredDeliveryTrips(status, pageable);
			return ResponseEntity.ok(deliveryTrips);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while fetching delivery trips: " + e.getMessage());
		}
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/by-shipment")
	public ResponseEntity<Page<DeliveryTrip>> getDeliveryTrips(@RequestParam int page, @RequestParam int size,
			@RequestParam String shipmentId) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("lastUpdatedStamp").descending());
		Page<DeliveryTrip> trips = deliveryTripService.getDeliveryTripsByShipmentId(shipmentId, pageable);
		return ResponseEntity.ok(trips);
	}

	@Secured("ROLE_WMS_DELIVERY_PERSON")
	@GetMapping("/today")
	public ResponseEntity<Page<TodayDeliveryTripResponse>> getTodayDeliveryTrips(Principal principal,
			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdStamp").descending());
		Page<TodayDeliveryTripResponse> trips = deliveryTripService.getTodayDeliveryTrips(principal.getName(), status,
				pageable);
		return ResponseEntity.ok(trips);
	}

	@Secured({ "ROLE_WMS_DELIVERY_PERSON", "ROLE_WMS_DELIVERY_MANAGER" })
	@GetMapping("/{deliveryTripId}/general-info")
	public ResponseEntity<DeliveryTripGeneralResponse> getDeliveryTripDetail(@PathVariable String deliveryTripId) {
		DeliveryTripGeneralResponse tripDetail = deliveryTripService.getDeliveryTripById(deliveryTripId);
		return ResponseEntity.ok(tripDetail);
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping
	public ResponseEntity<?> createDeliveryTrip(@RequestBody DeliveryTripCreateRequest payload, Principal principal) {
		try {
			DeliveryTrip trip = deliveryTripService.createDeliveryTrip(payload, principal.getName());
			return ResponseEntity.ok(trip);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating trip");
		}

	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/batch")
	public ResponseEntity<?> createMultipleDeliveryTrips(@RequestBody List<DeliveryTripCreateRequest> payloadList,
			Principal principal) {
		try {
			List<DeliveryTrip> trips = deliveryTripService.createDeliveryTrip(payloadList, principal.getName());
			return ResponseEntity.ok(trips);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating trips");
		}
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/{deliveryTripId}/cancel")
	public ResponseEntity<String> cancelDeliveryTrip(@PathVariable String deliveryTripId) {
		boolean cancelled = deliveryTripService.cancelDeliveryTrip(deliveryTripId);
		if (cancelled) {
			return ResponseEntity.ok("Delivery trip cancelled successfully.");
		} else {
			return ResponseEntity.badRequest().body("Delivery trip can only be cancelled if it is in CREATED status.");
		}
	}

	@Secured("ROLE_WMS_DELIVERY_PERSON")
	@PostMapping("/{deliveryTripId}/start")
	public ResponseEntity<String> startDeliveryTrip(@PathVariable String deliveryTripId) {
		boolean started = deliveryTripService.startDeliveryTrip(deliveryTripId);
		if (started) {
			return ResponseEntity.ok("Delivery trip started successfully.");
		} else {
			return ResponseEntity.badRequest().body("Delivery trip can only be started if it is in CREATED status.");
		}
	}

	@Secured("ROLE_WMS_DELIVERY_PERSON")
	@PostMapping("/{deliveryTripId}/mark-delivered")
	public ResponseEntity<String> markAsDelivered(@PathVariable String deliveryTripId, @RequestParam UUID orderId) {
		try {
			int updatedCount = deliveryTripService.markAsDelivered(deliveryTripId, orderId);
			return ResponseEntity.ok("Successfully updated " + updatedCount + " items as delivered.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
		}
	}

}
