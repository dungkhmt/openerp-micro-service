package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ProductCreateRequest;
import openerp.openerpresourceserver.dto.response.ProductDetailResponse;
import openerp.openerpresourceserver.dto.response.ProductGeneralResponse;
import openerp.openerpresourceserver.dto.response.ProductInventoryResponse;
import openerp.openerpresourceserver.dto.response.ProductNameResponse;
import openerp.openerpresourceserver.dto.response.ProductPriceResponse;
import openerp.openerpresourceserver.dto.response.ProductResponse;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.service.ProductService;

@RestController
@RequestMapping("/products")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

	private ProductService productService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public ResponseEntity<Page<ProductGeneralResponse>> getProductGeneralWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String search) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ProductGeneralResponse> products = productService.getAllProductGeneral(search, pageable);

		return ResponseEntity.ok(products);
	}

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@GetMapping("/inventory")
	public ResponseEntity<?> getProductInventoryWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String search, @RequestParam UUID warehouseId, boolean outOfStockOnly) {

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ProductInventoryResponse> products = productService.getAllProductInventory(search, pageable, warehouseId, outOfStockOnly);

			return ResponseEntity.ok(products);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
		}
		
	}

	@Secured("ROLE_WMS_ONLINE_CUSTOMER")
	@GetMapping("/public")
	public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) UUID categoryId, @RequestParam(defaultValue = "asc") String sortDir) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ProductResponse> products = productService.getProducts(pageable, searchTerm, categoryId, sortDir);
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured");
		}
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
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

	@Secured("ROLE_WMS_ONLINE_CUSTOMER")
	@GetMapping("/public/{id}")
	public ResponseEntity<?> getPublicProductDetail(@PathVariable("id") UUID productId) {
		try {
			ProductDetailResponse product = productService.getProductDetail(productId);

			if (product != null) {
				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured");
		}
	}

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@GetMapping("/names")
	public ResponseEntity<List<ProductNameResponse>> searchProducts(@RequestParam("search") String searchTerm) {
		List<ProductNameResponse> products = productService.searchProductNames(searchTerm);
		return ResponseEntity.ok(products);
	}

	@Secured("ROLE_WMS_SALE_MANAGER")
	@GetMapping("/price")
	public ResponseEntity<Page<ProductPriceResponse>> getProductsWithPrice(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ProductPriceResponse> productList = productService.getProductsWithPrice(pageable, search);

		return ResponseEntity.ok(productList);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@PostMapping
	public ResponseEntity<String> createProduct(@RequestParam String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductCreateRequest productDto = objectMapper.readValue(productData, ProductCreateRequest.class);

			// Tạo mới sản phẩm
			boolean isCreated = productService.createProduct(productDto, imageFile);

			if (isCreated) {
				return ResponseEntity.ok("Product created successfully");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Product code already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
		}
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@PostMapping("/update")
	public ResponseEntity<String> updateProduct(@RequestParam String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductCreateRequest productDto = objectMapper.readValue(productData, ProductCreateRequest.class);

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

}
