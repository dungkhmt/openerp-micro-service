package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ProductCreateRequest;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.projection.ProductDetailProjection;
import openerp.openerpresourceserver.projection.ProductGeneralProjection;
import openerp.openerpresourceserver.projection.ProductInventoryProjection;
import openerp.openerpresourceserver.projection.ProductNameProjection;
import openerp.openerpresourceserver.projection.ProductPriceProjection;
import openerp.openerpresourceserver.projection.ProductProjection;
import openerp.openerpresourceserver.service.ProductService;

@RestController
@RequestMapping("/products")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ProductController {

	private ProductService productService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public ResponseEntity<Page<ProductGeneralProjection>> getProductGeneralWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String search) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ProductGeneralProjection> products = productService.getAllProductGeneral(search, pageable);

		return ResponseEntity.ok(products);
	}

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@GetMapping("/inventory")
	public ResponseEntity<Page<ProductInventoryProjection>> getProductInventoryWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String search) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ProductInventoryProjection> products = productService.getAllProductInventory(search, pageable);

		return ResponseEntity.ok(products);
	}

	@Secured("ROLE_WMS_ONLINE_CUSTOMER")
	@GetMapping("/public")
	public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) UUID categoryId, @RequestParam(defaultValue = "asc") String sortDir) {
		try {
			Sort sort = Sort.by("price");
			sort = sortDir.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();
			Pageable pageable = PageRequest.of(page, size, sort);
			Page<ProductProjection> products = productService.getProducts(pageable, searchTerm, categoryId);
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi xử lý yêu cầu.");
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
			ProductDetailProjection product = productService.getProductDetail(productId);

			if (product != null) {
				return ResponseEntity.ok(product);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
		}
	}

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@GetMapping("/names")
	public ResponseEntity<List<ProductNameProjection>> searchProducts(@RequestParam("search") String searchTerm) {
		List<ProductNameProjection> products = productService.searchProductNames(searchTerm);
		return ResponseEntity.ok(products);
	}

	@Secured("ROLE_WMS_SALE_MANAGER")
	@GetMapping("/price")
	public ResponseEntity<Page<ProductPriceProjection>> getProductsWithPrice(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page, size);
		Page<ProductPriceProjection> productList = productService.getProductsWithPrice(pageable, search);

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
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating product");
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

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
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
