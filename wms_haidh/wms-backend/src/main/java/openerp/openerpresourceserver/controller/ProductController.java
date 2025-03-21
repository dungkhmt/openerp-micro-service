package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.projection.ProductInfoProjection;
import openerp.openerpresourceserver.entity.projection.ProductNameProjection;
import openerp.openerpresourceserver.model.request.ProductRequest;
import openerp.openerpresourceserver.service.ProductService;

@RestController
@RequestMapping("/products")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {
	
	private ProductService productService;
		
	@GetMapping
	public ResponseEntity<Page<ProductInfoProjection>> getProductInfoWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "search", required = false) String search) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ProductInfoProjection> products = productService.getAllProductGeneral(search, pageable);

		return ResponseEntity.ok(products);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductDetail(@PathVariable("id") UUID productId) {
	    try {
	        Product product = productService.getProductById(productId);

	        if (product != null) {
	            return ResponseEntity.ok(product);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
	        }
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
	    }
	}
	
	@GetMapping("/names")
	public ResponseEntity<List<ProductNameProjection>> searchProducts(@RequestParam("search") String searchTerm) {
		List<ProductNameProjection> products = productService.searchProductNames(searchTerm);
		return ResponseEntity.ok(products);
	}
	
	@PostMapping
	public ResponseEntity<String> createProduct(@RequestParam("productData") String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductRequest productDto = objectMapper.readValue(productData, ProductRequest.class);

			// Tạo mới sản phẩm
			boolean isCreated = productService.createProduct(productDto, imageFile);

			if (isCreated) {
				return ResponseEntity.ok("Product created successfully");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
		}
	}

	
	@PostMapping("/update")
	public ResponseEntity<String> updateProduct(@RequestParam("productData") String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductRequest productDto = objectMapper.readValue(productData, ProductRequest.class);

			// Cập nhật sản phẩm
			boolean isUpdated = productService.updateProduct(productDto, imageFile);

			if (isUpdated) {
				return ResponseEntity.ok("Product updated successfully");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product");
		}
	}

	@PostMapping("/delete")
	public ResponseEntity<String> deleteProduct(@RequestBody Map<String, Object> requestBody) {
		try {
			String id = (String) requestBody.get("id");
			UUID uuid = UUID.fromString(id); // Convert the string to UUID
			boolean isDeleted = productService.deleteProductById(uuid);
			if (isDeleted) {
				return ResponseEntity.ok("Product deleted successfully");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
		}
	}
	
}
