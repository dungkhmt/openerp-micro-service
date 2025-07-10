package openerp.openerpresourceserver.controller;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ReceiptCreateRequest;
import openerp.openerpresourceserver.dto.response.ReceiptInfoResponse;
import openerp.openerpresourceserver.dto.response.ReceiptResponse;
import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.service.ReceiptItemRequestService;
import openerp.openerpresourceserver.service.ReceiptService;

@RestController
@RequestMapping("/receipts")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptController {

	private ReceiptService receiptService;
	private ReceiptItemRequestService receiptItemRequestService;

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_PURCHASE_STAFF","ROLE_WMS_PURCHASE_MANAGER"})
	@GetMapping
	public ResponseEntity<Page<ReceiptInfoResponse>> getReceiptsByStatus(

			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			Page<ReceiptInfoResponse> receipts = receiptService.searchReceipts(status, pageable);
			return ResponseEntity.ok(receipts);
		} catch (Exception e) {
			// Log the error for further investigation
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_PURCHASE_STAFF","ROLE_WMS_PURCHASE_MANAGER"})
	@GetMapping("/{id}")
	public ResponseEntity<ReceiptResponse> getReceiptDetailsById(@PathVariable UUID id) {
        return receiptService.getReceiptDetailsById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

	@Secured("ROLE_WMS_PURCHASE_STAFF")
	@PostMapping
	public ResponseEntity<String> createReceipt(@RequestBody ReceiptCreateRequest receiptRequest, Principal principal) {
		try {
			// Validate item requests
			if (receiptRequest.getReceiptItemRequests() == null || receiptRequest.getReceiptItemRequests().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receipt must contain at least one item.");
			}

			// Proceed with creating receipt
			Receipt createdReceipt = receiptService.createReceipt(receiptRequest, principal.getName());

			// Create receipt item requests
			receiptItemRequestService.createReceiptItems(createdReceipt.getReceiptId(),
					receiptRequest.getReceiptItemRequests());

			return ResponseEntity.ok("Receipt created successfully with ID: " + createdReceipt.getReceiptId());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating receipt");
		}
	}

	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@PostMapping("/{receiptId}/approve")
	public ResponseEntity<String> approveReceipt(@PathVariable UUID receiptId, Principal principal) {
		try {
			boolean isApproved = receiptService.approveReceipt(receiptId, principal.getName());

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

	@Secured("ROLE_WMS_PURCHASE_MANAGER")
	@PostMapping("/{receiptId}/cancel")
	public ResponseEntity<String> cancelReceipt(@PathVariable UUID receiptId, Principal principal) {
		try {
			boolean isCancelled = receiptService.cancelReceipt(receiptId, principal.getName());

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
