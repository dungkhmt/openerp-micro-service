package openerp.openerpresourceserver.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import openerp.openerpresourceserver.dto.request.AssignedOrderItemCreateRequest;
import openerp.openerpresourceserver.dto.response.AssignedOrderItemResponse;
import openerp.openerpresourceserver.dto.response.DeliveryOrderItemResponse;
import openerp.openerpresourceserver.dto.response.PickedOrderItemResponse;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.service.AssignedOrderItemService;

@RestController
@RequestMapping("/assigned-order-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssignedOrderItemController {

	private AssignedOrderItemService assignedOrderItemService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public List<AssignedOrderItemResponse> getAssignedOrderItemsBySaleOrderItemId(
			@RequestParam UUID saleOrderItemId) {
		return assignedOrderItemService.getAssignedOrderItemsBySaleOrderItemId(saleOrderItemId);
	}

	@Secured("ROLE_WMS_WAREHOUSE_STAFF")
	@GetMapping("/assigned")
	public ResponseEntity<Page<PickedOrderItemResponse>> getAssignedOrderItems(@RequestParam UUID bayId,
			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<PickedOrderItemResponse> orderItems = assignedOrderItemService.getAllPickedOrderItems(bayId, status,
				pageable);
		return ResponseEntity.ok(orderItems);
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/picked")
	public ResponseEntity<Page<DeliveryOrderItemResponse>> getPickedAssignedOrderItems(@RequestParam UUID warehouseId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<DeliveryOrderItemResponse> orderItems = assignedOrderItemService.getAllDeliveryOrderItems(warehouseId,
				pageable);
		return ResponseEntity.ok(orderItems);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@PostMapping
	public ResponseEntity<AssignedOrderItem> assignOrderItem(@RequestBody AssignedOrderItemCreateRequest dto,
			Principal principal) {
		try {
			AssignedOrderItem assignedOrderItem = assignedOrderItemService.assignOrderItem(dto, principal.getName());
			return ResponseEntity.ok(assignedOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

		}
	}

	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@PostMapping("/delivery-items")
	public ResponseEntity<List<DeliveryOrderItemResponse>> getDeliveryOrderItems(
			@RequestBody List<UUID> assignedOrderItemIds) {
		List<DeliveryOrderItemResponse> items = assignedOrderItemService.getDeliveryOrderItems(assignedOrderItemIds);
		return ResponseEntity.ok(items);
	}

	@Secured("ROLE_WMS_WAREHOUSE_STAFF")
	@PostMapping("/{id}/mark-as-picked")
	public ResponseEntity<String> markAsPicked(@PathVariable("id") UUID assignedOrderItemId) {
		assignedOrderItemService.markAsPicked(assignedOrderItemId);
		return ResponseEntity.ok("Marked as PICKED");
	}

}
