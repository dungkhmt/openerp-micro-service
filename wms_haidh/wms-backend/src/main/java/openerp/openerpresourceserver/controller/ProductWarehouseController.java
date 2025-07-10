package openerp.openerpresourceserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.ProductAvailabilityRequest;
import openerp.openerpresourceserver.service.ProductWarehouseService;

@RestController
@RequestMapping("/product-warehouses")
@RequiredArgsConstructor
public class ProductWarehouseController {

	private final ProductWarehouseService service;

	@PostMapping("/check-inventory")
    public ResponseEntity<Boolean> checkAvailability(@RequestBody ProductAvailabilityRequest request) {
        boolean available = service.isProductAvailable(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(available);
    }
}
