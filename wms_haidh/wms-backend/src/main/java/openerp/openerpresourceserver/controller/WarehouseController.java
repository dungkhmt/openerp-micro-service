package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.service.WarehouseService;

@RestController
@RequestMapping("/warehouses")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class WarehouseController {

	private WarehouseService warehouseService;

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_PURCHASE_STAFF","ROLE_WMS_DELIVERY_MANAGER"})
	@GetMapping
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}
	
	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_DELIVERY_MANAGER","ROLE_WMS_WAREHOUSE_STAFF"})
	@GetMapping("/{id}")
    public ResponseEntity<Warehouse> getWarehouseById(@PathVariable("id") UUID warehouseId) {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        return ResponseEntity.ok(warehouse);
    }

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_WAREHOUSE_STAFF"})
	@GetMapping("/paged")
	public ResponseEntity<Page<Warehouse>> getWarehouses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Warehouse> result = warehouseService.getWarehouses(search, pageable);
		return ResponseEntity.ok(result);
	}
	
	@Secured("ROLE_WMS_DELIVERY_MANAGER")
	@GetMapping("/with-picked-assigned-items")
    public ResponseEntity<List<Warehouse>> getRelevantWarehouses() {
        List<Warehouse> warehouses = warehouseService.getWarehousesWithPickedOrders();
        return ResponseEntity.ok(warehouses);
    }

}
