package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Bay;
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.service.BayService;

@RestController
@RequestMapping("/bays")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BayController {

	private BayService bayService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public ResponseEntity<List<BayProjection>> getAllBays(@RequestParam UUID warehouseId) {
		List<BayProjection> warehouses = bayService.getBaysProjectionByWarehouseId(warehouseId);
		return ResponseEntity.ok(warehouses);
	}
	
	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/full")
    public ResponseEntity<List<Bay>> getBaysByWarehouseId(@RequestParam UUID warehouseId) {
        List<Bay> bays = bayService.getBaysByWarehouseId(warehouseId);
        return ResponseEntity.ok(bays);
    }
	
	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}")
    public ResponseEntity<Bay> getBayById(@PathVariable("id") UUID bayId) {
        Bay bay = bayService.getBayById(bayId);
        return ResponseEntity.ok(bay);
    }
}
