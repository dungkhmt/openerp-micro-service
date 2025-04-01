package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ReceiptItemCreateRequest;
import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.projection.ReceiptItemProjection;
import openerp.openerpresourceserver.service.ReceiptItemService;

@RestController
@RequestMapping("/receipt-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptItemController {

	private ReceiptItemService receiptItemService;
	
	@GetMapping
	public ResponseEntity<List<ReceiptItemProjection>> getItemsByReceiptItemRequestId(@RequestParam UUID requestId) {
	    List<ReceiptItemProjection> items = receiptItemService.getItemsByRequestId(requestId);
	    return ResponseEntity.ok(items);
	}

	@PostMapping
	public ResponseEntity<?> createReceiptItem(@RequestBody ReceiptItemCreateRequest request) {
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

}
