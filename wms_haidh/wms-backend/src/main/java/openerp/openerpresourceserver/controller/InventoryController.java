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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.projection.InventoryItemProjection;
import openerp.openerpresourceserver.projection.LotIdProjection;
import openerp.openerpresourceserver.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)

public class InventoryController {

	private InventoryService inventoryService;
	
	@GetMapping
	public ResponseEntity<Page<InventoryItemProjection>> getInventoryItems(
			@RequestParam(required = false) UUID warehouseId, @RequestParam(required = false) UUID bayId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {

			Pageable pageable = PageRequest.of(page, size);
			Page<InventoryItemProjection> inventoryItems = inventoryService.getInventoryItems(warehouseId, bayId,
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
	
	@GetMapping("/available-warehouses")
	public List<Warehouse> getDistinctWarehousesWithStock(@RequestParam UUID saleOrderItemId) {
		return inventoryService.getDistinctWarehousesWithStockBySaleOrderItemId(saleOrderItemId);
	}

	@GetMapping("/available-bays")
	public ResponseEntity<List<BayProjection>> getBaysWithProductsInSaleOrder(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID warehouseId) {
		List<BayProjection> bays = inventoryService.getBaysWithProductsInSaleOrder(saleOrderItemId, warehouseId);
		return ResponseEntity.ok(bays);
	}

	@GetMapping("/available-lots")
	public ResponseEntity<List<LotIdProjection>> getLotIdsBySaleOrderItemIdAndBayId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId) {
		List<LotIdProjection> lotIds = inventoryService.getLotIdsBySaleOrderItemIdAndBayId(saleOrderItemId, bayId);
		return ResponseEntity.ok(lotIds);
	}

	@GetMapping("/quantity-on-hand")
	public ResponseEntity<Integer> getQuantityOnHandBySaleOrderItemIdBayIdAndLotId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId, @RequestParam String lotId) {
		return inventoryService.getQuantityOnHandBySaleOrderItemIdBayIdAndLotId(saleOrderItemId, bayId, lotId)
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

}
