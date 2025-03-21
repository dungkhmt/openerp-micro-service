package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.projection.AssignedOrderItemProjection;
import openerp.openerpresourceserver.entity.projection.DeliveryOrderItemProjection;
import openerp.openerpresourceserver.model.request.AssignedOrderItemRequest;
import openerp.openerpresourceserver.service.AssignedOrderItemService;

@RestController
@RequestMapping("/assigned-order-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AssignedOrderItemController {

	private AssignedOrderItemService assignedOrderItemService;
	
	@GetMapping
	public List<AssignedOrderItemProjection> getAssignedOrderItemsBySaleOrderItemId(
			@RequestParam UUID saleOrderItemId) {
		return assignedOrderItemService.getAssignedOrderItemsBySaleOrderItemId(saleOrderItemId);
	}
	
	@GetMapping("/by-warehouse")
	public ResponseEntity<Page<DeliveryOrderItemProjection>> getAssignedOrderItemsByWarehouseId(@RequestParam UUID warehouseId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<DeliveryOrderItemProjection> orderItems = assignedOrderItemService.getAllDeliveryOrderItems(warehouseId,
				pageable);
		return ResponseEntity.ok(orderItems);
	}

	@PostMapping
	public ResponseEntity<AssignedOrderItem> assignOrderItem(@RequestBody AssignedOrderItemRequest dto) {
		try {
			AssignedOrderItem assignedOrderItem = assignedOrderItemService.assignOrderItem(dto);
			return ResponseEntity.ok(assignedOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

		}
	}
	

}
