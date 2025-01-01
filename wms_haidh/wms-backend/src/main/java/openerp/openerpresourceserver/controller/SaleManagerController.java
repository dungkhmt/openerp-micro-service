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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.entity.projection.ReceiptInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemDetailProjection;
import openerp.openerpresourceserver.service.ReceiptService;

@RestController
@RequestMapping("/sale-manager")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SaleManagerController {

	private ReceiptService receiptService;

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

	@PostMapping("/approve/{receiptId}")
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

	@PostMapping("/cancel/{receiptId}")
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
