package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import openerp.openerpresourceserver.dto.request.SupplierCreateRequest;
import openerp.openerpresourceserver.entity.Supplier;
import openerp.openerpresourceserver.service.SupplierService;

@RestController
@RequestMapping("/suppliers")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SupplierController {

	private SupplierService supplierService;

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@GetMapping
	public ResponseEntity<List<Supplier>> getAllSuppliers() {
		List<Supplier> suppliers = supplierService.getAllSuppliers();
		return ResponseEntity.ok(suppliers);
	}

	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@GetMapping("/paged")
	public ResponseEntity<Page<Supplier>> searchSuppliers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateUpdated"));
		Page<Supplier> suppliers = supplierService.searchSuppliers(search, pageable);

		return ResponseEntity.ok(suppliers);

	}
	
	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@GetMapping("/{id}")
	public ResponseEntity<Supplier> getSupplierById(@PathVariable UUID id) {
		return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@PostMapping
	public ResponseEntity<Supplier> createSupplier(@RequestBody SupplierCreateRequest dto) {
		Supplier supplier = supplierService.createSupplier(dto);
		return ResponseEntity.ok(supplier);

	}

	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@PostMapping("/update")
	public ResponseEntity<Supplier> updateSupplier(@RequestBody Supplier updatedSupplier) {
		Supplier supplier = supplierService.updateSupplier(updatedSupplier);
		return ResponseEntity.ok(supplier);
	}
}
