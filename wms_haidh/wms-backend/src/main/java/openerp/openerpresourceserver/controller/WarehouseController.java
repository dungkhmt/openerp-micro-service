package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@GetMapping
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}


}
