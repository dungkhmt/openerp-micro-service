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
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.entity.projection.BayProjection;
import openerp.openerpresourceserver.entity.projection.ProductInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemRequestProjection;
import openerp.openerpresourceserver.model.request.ProductCreate;
import openerp.openerpresourceserver.service.ProductCategoryService;
import openerp.openerpresourceserver.service.ProductService;
import openerp.openerpresourceserver.service.ReceiptItemRequestService;
import openerp.openerpresourceserver.service.ReceiptItemService;
import openerp.openerpresourceserver.service.WarehouseService;

@RestController
@RequestMapping("/admin")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AdminController {

	private ProductService productService;
	private ProductCategoryService productCategoryService;
	private WarehouseService warehouseService;
	private ReceiptItemRequestService receiptItemRequestService;
	private ReceiptItemService receiptItemService;

	@GetMapping("/warehouse")
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}

	@GetMapping("/product")
	public ResponseEntity<Page<ProductInfoProjection>> getProductInfoWithPaging(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(value = "search", required = false) String search) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ProductInfoProjection> products = productService.getAllProductGeneral(search, pageable);

		return ResponseEntity.ok(products);
	}

	@PostMapping("/product/create-product")
	public ResponseEntity<String> createProduct(@RequestParam("productData") String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductCreate productDto = objectMapper.readValue(productData, ProductCreate.class);

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

	@PostMapping("/product/delete-product")
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

	@GetMapping("/product/category")
	public ResponseEntity<List<ProductCategory>> getAll() {
		return ResponseEntity.ok(productCategoryService.getAll());
	}

	@PostMapping("/product/get-product-detail")
	public ResponseEntity<?> getProductDetail(@RequestParam("id") String id) {
		try {
			UUID productId = UUID.fromString(id);

			// Fetch product details from the service
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

	@PostMapping("/product/update-product")
	public ResponseEntity<String> updateProduct(@RequestParam("productData") String productData,
			@RequestParam(value = "image", required = false) MultipartFile imageFile) {
		try {
			// Deserialize product data
			ObjectMapper objectMapper = new ObjectMapper();
			ProductCreate productDto = objectMapper.readValue(productData, ProductCreate.class);

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

	@GetMapping("/receipt")
	public ResponseEntity<Page<ReceiptItemRequestProjection>> getAllReceiptItems(
			@RequestParam(defaultValue = "APPROVED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ReceiptItemRequestProjection> receiptItems = receiptItemRequestService.getAllReceiptItemRequests(status,
				pageable);
		return ResponseEntity.ok(receiptItems);
	}

	@GetMapping("/receipt/bay/{id}")
	public ResponseEntity<List<BayProjection>> getBaysByReceiptItemRequestId(@PathVariable UUID id) {
		return ResponseEntity.ok(receiptItemRequestService.getBaysByReceiptItemRequestId(id));
	}

	@GetMapping("/receipt/general-info/{id}")
	public ResponseEntity<ReceiptItemRequestProjection> getReceiptItemDetail(@PathVariable UUID id) {
		return receiptItemRequestService.getReceiptItemRequestDetail(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/receipt/detail-info/{id}")
	public ResponseEntity<List<ReceiptItemProjection>> getItemsByReceiptItemRequestId(@PathVariable UUID id) {
		List<ReceiptItemProjection> items = receiptItemService.getItemsByRequestId(id);
		return ResponseEntity.ok(items);
	}
}
