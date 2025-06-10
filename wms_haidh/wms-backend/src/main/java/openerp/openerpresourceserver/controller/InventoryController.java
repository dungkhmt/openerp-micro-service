package openerp.openerpresourceserver.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.BayResponse;
import openerp.openerpresourceserver.dto.response.InventoryItemResponse;
import openerp.openerpresourceserver.dto.response.LotIdResponse;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)

public class InventoryController {

	private InventoryService inventoryService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public ResponseEntity<Page<InventoryItemResponse>> getInventoryItems(@RequestParam UUID bayId,
			@RequestParam(required = false) String lotId, @RequestParam(required = false) String search,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {

			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastUpdatedStamp"));
			Page<InventoryItemResponse> inventoryItems = inventoryService.getInventoryItems(bayId, lotId, search,
					pageable);

			return ResponseEntity.ok(inventoryItems);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid request parameters: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			System.err.println("Unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/available-warehouses")
	public List<Warehouse> getDistinctWarehousesWithStock(@RequestParam UUID saleOrderItemId) {
		return inventoryService.getDistinctWarehousesWithStockBySaleOrderItemId(saleOrderItemId);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/available-bays")
	public ResponseEntity<List<BayResponse>> getBaysWithProductsInSaleOrder(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID warehouseId) {
		List<BayResponse> bays = inventoryService.getBaysWithProductsInSaleOrder(saleOrderItemId, warehouseId);
		return ResponseEntity.ok(bays);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/available-lots")
	public ResponseEntity<List<LotIdResponse>> getLotIdsBySaleOrderItemIdAndBayId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId) {
		List<LotIdResponse> lotIds = inventoryService.getLotIdsBySaleOrderItemIdAndBayId(saleOrderItemId, bayId);
		return ResponseEntity.ok(lotIds);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/available-quantity")
	public ResponseEntity<Integer> getAvailableQuantityBySaleOrderItemIdBayIdAndLotId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId, @RequestParam String lotId) {
		return inventoryService.getAvailableQuantityBySaleOrderItemIdBayIdAndLotId(saleOrderItemId, bayId, lotId)
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/lot-ids")
	public ResponseEntity<List<String>> getLotIdsByBayId(@RequestParam UUID bayId) {
		List<String> lotIds = inventoryService.getDistinctLotIdsByBayId(bayId);
		return ResponseEntity.ok(lotIds);
	}

}
