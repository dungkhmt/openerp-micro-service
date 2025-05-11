package openerp.openerpresourceserver.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ProductPriceCreateRequest;
import openerp.openerpresourceserver.entity.ProductPrice;
import openerp.openerpresourceserver.service.ProductPriceService;

@RestController
@RequestMapping("/product-prices")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductPriceController {

	private ProductPriceService productPriceService;

	@Secured("ROLE_WMS_SALE_MANAGER")
	@GetMapping
	public Page<ProductPrice> getProductPrices(@RequestParam UUID productId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productPriceService.getProductPricesByProductId(productId, pageable);
	}
	
	@Secured("ROLE_WMS_SALE_MANAGER")
	@PostMapping
    public ResponseEntity<ProductPrice> createProductPrice(@RequestBody ProductPriceCreateRequest request) {
        ProductPrice created = productPriceService.createProductPrice(request);
        return ResponseEntity.ok(created);
    }

}
