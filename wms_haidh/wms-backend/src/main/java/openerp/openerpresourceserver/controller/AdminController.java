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
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductCategory;
import openerp.openerpresourceserver.entity.ReceiptBill;
import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.entity.projection.AssignedOrderItemProjection;
import openerp.openerpresourceserver.entity.projection.BayProjection;
import openerp.openerpresourceserver.entity.projection.InventoryItemProjection;
import openerp.openerpresourceserver.entity.projection.LotIdProjection;
import openerp.openerpresourceserver.entity.projection.ProductInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemRequestProjection;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemDetailProjection;
import openerp.openerpresourceserver.entity.projection.SaleOrderItemProjection;
import openerp.openerpresourceserver.model.request.AssignedOrderItemCreate;
import openerp.openerpresourceserver.model.request.ProductCreate;
import openerp.openerpresourceserver.model.request.ReceiptBillCreate;
import openerp.openerpresourceserver.model.request.ReceiptItemCreate;
import openerp.openerpresourceserver.service.AssignedOrderItemService;
import openerp.openerpresourceserver.service.BayService;
import openerp.openerpresourceserver.service.InventoryService;
import openerp.openerpresourceserver.service.ProductCategoryService;
import openerp.openerpresourceserver.service.ProductService;
import openerp.openerpresourceserver.service.ReceiptBillService;
import openerp.openerpresourceserver.service.ReceiptItemRequestService;
import openerp.openerpresourceserver.service.ReceiptItemService;
import openerp.openerpresourceserver.service.SaleOrderItemService;
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
	private ReceiptBillService receiptBillService;
	private InventoryService inventoryService;
	private BayService bayService;
	private SaleOrderItemService saleOrderItemService;
	private AssignedOrderItemService assignedOrderItemService;

	@GetMapping("/warehouse")
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}

	@GetMapping("/bay")
	public ResponseEntity<List<BayProjection>> getAllBays(@RequestParam("warehouseId") UUID warehouseId) {
		List<BayProjection> warehouses = bayService.getBaysByWarehouseId(warehouseId);
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

	@GetMapping("/receipt/{id}")
	public ResponseEntity<List<ReceiptItemRequestProjection>> getAllReceiptItems(@PathVariable UUID id) {
		List<ReceiptItemRequestProjection> receiptItems = receiptItemRequestService.getAllReceiptItemRequests(id);
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

	@GetMapping("/receipt/bill/{id}")
	public ResponseEntity<List<String>> getAllReceiptBillIds(@PathVariable UUID id) {
		List<String> receiptBillIds = receiptBillService.getAllReceiptBillIds(id);
		return ResponseEntity.ok(receiptBillIds);
	}

	@PostMapping("/receipt/bill/create")
	public ResponseEntity<ReceiptBill> createReceiptBill(@RequestBody ReceiptBillCreate request) {
		ReceiptBill receiptBill = receiptBillService.createReceiptBill(request.getReceiptBillId(),
				request.getDescription(), request.getCreatedBy(), request.getReceiptItemRequestId());
		return ResponseEntity.ok(receiptBill);
	}

	@PostMapping("/receipt/receipt-item/create")
	public ResponseEntity<?> createReceiptItem(@RequestBody ReceiptItemCreate request) {
		try {
			ReceiptItem receiptItem = receiptItemService.createReceiptItem(request);
			return ResponseEntity.ok(receiptItem);
		} catch (IllegalArgumentException e) {
			// Xử lý ngoại lệ nếu dữ liệu đầu vào không hợp lệ
			return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
		} catch (Exception e) {
			// Xử lý ngoại lệ chung
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	@GetMapping("/inventory")
	public ResponseEntity<Page<InventoryItemProjection>> getInventoryItems(
			@RequestParam(required = false) UUID warehouseId, @RequestParam(required = false) UUID bayId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		try {

			Pageable pageable = PageRequest.of(page, size);
			Page<InventoryItemProjection> inventoryItems = inventoryService.getInventoryItems(warehouseId, bayId,
					pageable);

			return ResponseEntity.ok(inventoryItems);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid request parameters: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			System.err.println("Unexpected error occurred: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/orders/{id}")
	public List<SaleOrderItemProjection> getAllSaleOrderItems(@PathVariable UUID id) {
		return saleOrderItemService.getSaleOrderItems(id);
	}

	@GetMapping("/orders/general-info/{id}")
	public SaleOrderItemDetailProjection getSaleOrderItemDetail(@PathVariable UUID id) {
		return saleOrderItemService.getSaleOrderItemDetail(id);
	}

	@GetMapping("/orders/detail-info/{saleOrderItemId}")
	public List<AssignedOrderItemProjection> getAssignedOrderItemsBySaleOrderItemId(
			@PathVariable UUID saleOrderItemId) {
		return assignedOrderItemService.getAssignedOrderItemsBySaleOrderItemId(saleOrderItemId);
	}

	@GetMapping("/orders/warehouses/{saleOrderItemId}")
	public List<Warehouse> getDistinctWarehousesWithStock(@PathVariable UUID saleOrderItemId) {
		return inventoryService.getDistinctWarehousesWithStockBySaleOrderItemId(saleOrderItemId);
	}

	@GetMapping("/orders/bays")
	public ResponseEntity<List<BayProjection>> getBaysWithProductsInSaleOrder(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID warehouseId) {
		List<BayProjection> bays = inventoryService.getBaysWithProductsInSaleOrder(saleOrderItemId, warehouseId);
		return ResponseEntity.ok(bays);
	}

	@GetMapping("/orders/lots")
	public ResponseEntity<List<LotIdProjection>> getLotIdsBySaleOrderItemIdAndBayId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId) {
		List<LotIdProjection> lotIds = inventoryService.getLotIdsBySaleOrderItemIdAndBayId(saleOrderItemId, bayId);
		return ResponseEntity.ok(lotIds);
	}

	@GetMapping("/orders/quantity")
	public ResponseEntity<Integer> getQuantityOnHandBySaleOrderItemIdBayIdAndLotId(@RequestParam UUID saleOrderItemId,
			@RequestParam UUID bayId, @RequestParam String lotId) {
		return inventoryService.getQuantityOnHandBySaleOrderItemIdBayIdAndLotId(saleOrderItemId, bayId, lotId)
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/orders/assign")
	public ResponseEntity<AssignedOrderItem> assignOrderItem(@RequestBody AssignedOrderItemCreate dto) {
		try {
			AssignedOrderItem assignedOrderItem = assignedOrderItemService.assignOrderItem(dto);
			return ResponseEntity.ok(assignedOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

		}
	}

}
