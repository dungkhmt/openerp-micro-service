package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.CustomerDeliveryResponse;
import openerp.openerpresourceserver.dto.response.DeliveryItemDetailResponse;
import openerp.openerpresourceserver.service.DeliveryTripItemService;

@RestController
@RequestMapping("/delivery-trip-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryTripItemController {

	private DeliveryTripItemService deliveryTripItemService;

	@Secured({ "ROLE_WMS_DELIVERY_PERSON", "ROLE_WMS_DELIVERY_MANAGER" })
	@GetMapping
	public ResponseEntity<Page<DeliveryItemDetailResponse>> getDeliveryItems(@RequestParam String deliveryTripId,
			@RequestParam UUID orderId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<DeliveryItemDetailResponse> items = deliveryTripItemService.getDeliveryItems(deliveryTripId, orderId,
				page, size);
		return ResponseEntity.ok(items);
	}

	@Secured({ "ROLE_WMS_DELIVERY_PERSON", "ROLE_WMS_DELIVERY_MANAGER" })
	@GetMapping("/customers")
	public ResponseEntity<List<CustomerDeliveryResponse>> getCustomers(@RequestParam String deliveryTripId) {
		List<CustomerDeliveryResponse> customers = deliveryTripItemService
				.getCustomersByDeliveryTripId(deliveryTripId);
		return ResponseEntity.ok(customers);
	}

}
