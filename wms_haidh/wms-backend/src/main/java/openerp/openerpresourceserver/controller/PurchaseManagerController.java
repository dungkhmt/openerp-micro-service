package openerp.openerpresourceserver.controller;

import java.util.ArrayList;
import java.util.List;
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

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.entity.projection.ProductNameProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptBillProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemDetailProjection;
import openerp.openerpresourceserver.model.request.ReceiptRequest;
import openerp.openerpresourceserver.service.ProductService;
import openerp.openerpresourceserver.service.ReceiptBillService;
import openerp.openerpresourceserver.service.ReceiptService;
import openerp.openerpresourceserver.service.WarehouseService;

@RestController
@RequestMapping("/purchase-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PurchaseManagerController {

	private ReceiptService receiptService;
	private ReceiptBillService receiptBillService;
	private WarehouseService warehouseService;
	private ProductService productService;

	@GetMapping("/receipts")
	public ResponseEntity<Page<ReceiptInfoProjection>> getReceiptsByStatus(

			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ReceiptInfoProjection> receipts = receiptService.searchReceipts(status, pageable);
			return ResponseEntity.ok(receipts);
		} catch (Exception e) {
			// Log the error for further investigation
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	@GetMapping("/receipts/{receiptId}")
	public ResponseEntity<List<ReceiptItemDetailProjection>> getReceiptItemDetails(@PathVariable UUID receiptId) {
		try {
			List<ReceiptItemDetailProjection> receiptItemDetails = receiptService.getReceiptItemDetails(receiptId);

			if (receiptItemDetails.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
			}
			return ResponseEntity.ok(receiptItemDetails);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
		}
	}

	@PostMapping("/receipts/create-receipt")
	public ResponseEntity<String> createReceipt(@RequestBody ReceiptRequest receiptRequest) {
	    try {
	        // Validate item requests
	        if (receiptRequest.getReceiptItemRequests() == null || receiptRequest.getReceiptItemRequests().isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receipt must contain at least one item.");
	        }

	        // Optionally validate or process the LocalDateTime values (e.g., null checks or defaults)
	        if (receiptRequest.getReceiptDate() == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receipt date cannot be null.");
	        }

	        // Proceed with creating receipt
	        Receipt createdReceipt = receiptService.createReceipt(receiptRequest);

	        // Create receipt item requests
	        receiptService.createReceiptItems(createdReceipt.getReceiptId(), receiptRequest.getReceiptItemRequests());

	        return ResponseEntity.ok("Receipt created successfully with ID: " + createdReceipt.getReceiptId());
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating receipt");
	    }
	}


	@GetMapping("/receipt-bill")
	public ResponseEntity<Page<ReceiptBillProjection>> getAllReceiptBills(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {

		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ReceiptBillProjection> receiptBills = receiptBillService.getAllReceiptBills(pageable);

			return ResponseEntity.ok(receiptBills);
		} catch (Exception e) {
			// Log the error for further investigation
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/receipts/warehouse")
	public ResponseEntity<List<Warehouse>> getAllWarehouses() {
		List<Warehouse> warehouses = warehouseService.getAllWarehouses();
		return ResponseEntity.ok(warehouses);
	}

	@GetMapping("/receipts/product")
	public ResponseEntity<List<ProductNameProjection>> searchProducts(@RequestParam("search") String searchTerm) {
		List<ProductNameProjection> products = productService.searchProductNames(searchTerm);
		return ResponseEntity.ok(products);
	}

	@PostMapping("/process-receipts/approve/{receiptId}")
	public ResponseEntity<String> approveReceipt(@PathVariable UUID receiptId, @RequestParam String approvedBy) {
		try {
			boolean isApproved = receiptService.approveReceipt(receiptId, approvedBy);

			if (isApproved) {
				return ResponseEntity.ok("Receipt approved successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving receipt.");
		}
	}

	@PostMapping("/process-receipts/cancel/{receiptId}")
	public ResponseEntity<String> cancelReceipt(@PathVariable UUID receiptId, @RequestParam String cancelledBy) {
		try {
			boolean isCancelled = receiptService.cancelReceipt(receiptId, cancelledBy);

			if (isCancelled) {
				return ResponseEntity.ok("Receipt cancelled successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receipt not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error cancelling receipt.");
		}
	}


}
