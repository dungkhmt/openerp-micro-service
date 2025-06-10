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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ReceiptItemCreateRequest;
import openerp.openerpresourceserver.dto.response.PutawayItemResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemResponse;
import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.service.ReceiptItemService;

@RestController
@RequestMapping("/receipt-items")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptItemController {

	private ReceiptItemService receiptItemService;

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping
	public ResponseEntity<List<ReceiptItemResponse>> getItemsByReceiptItemRequestId(@RequestParam UUID requestId) {
		List<ReceiptItemResponse> items = receiptItemService.getItemsByRequestId(requestId);
		return ResponseEntity.ok(items);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
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

	@Secured("ROLE_WMS_WAREHOUSE_STAFF")
	@GetMapping("/assigned")
	public Page<PutawayItemResponse> getReceiptItems(@RequestParam UUID bayId,
			@RequestParam(defaultValue = "CREATED") String status, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return receiptItemService.getPutawayItems(bayId, status, pageable);
	}
	
	@Secured("ROLE_WMS_WAREHOUSE_STAFF")
	@PostMapping("/{id}/mark-as-putawayed")
	public ResponseEntity<String> markAsPutawayed(@PathVariable("id") UUID receiptItemId) {
		receiptItemService.markAsPutawayed(receiptItemId);
		return ResponseEntity.ok("Marked as PUTAWAYED");
	}

}
