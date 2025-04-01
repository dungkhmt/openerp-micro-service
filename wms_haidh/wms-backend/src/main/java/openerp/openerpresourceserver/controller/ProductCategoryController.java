package openerp.openerpresourceserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.service.ProductCategoryService;

@RestController
@RequestMapping("/categories")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductCategoryController {
	
	private ProductCategoryService productCategoryService;
	
	@GetMapping
	public ResponseEntity<List<ProductCategory>> getAll() {
		return ResponseEntity.ok(productCategoryService.getAll());
	}

}
