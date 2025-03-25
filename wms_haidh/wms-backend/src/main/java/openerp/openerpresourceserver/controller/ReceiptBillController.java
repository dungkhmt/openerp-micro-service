package openerp.openerpresourceserver.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.request.ReceiptBillCreateRequest;
import openerp.openerpresourceserver.entity.ReceiptBill;
import openerp.openerpresourceserver.projection.ReceiptBillProjection;
import openerp.openerpresourceserver.service.ReceiptBillService;

@RestController
@RequestMapping("/receipt-bills")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptBillController {

	private ReceiptBillService receiptBillService;
	
	@GetMapping
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
	
	@GetMapping("/ids")
	public ResponseEntity<List<String>> getAllReceiptBillIds(@RequestParam UUID requestId) {
	    List<String> receiptBillIds = receiptBillService.getAllReceiptBillIds(requestId);
	    return ResponseEntity.ok(receiptBillIds);
	}
	
	@PostMapping
	public ResponseEntity<ReceiptBill> createReceiptBill(@RequestBody ReceiptBillCreateRequest request) {
		ReceiptBill receiptBill = receiptBillService.createReceiptBill(request.getReceiptBillId(),
				request.getDescription(), request.getCreatedBy(), request.getReceiptItemRequestId());
		return ResponseEntity.ok(receiptBill);
	}
	

}
