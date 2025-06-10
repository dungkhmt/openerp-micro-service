package openerp.openerpresourceserver.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.dto.response.BayResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemRequestDetailResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemRequestResponse;
import openerp.openerpresourceserver.service.ReceiptItemRequestService;

@RestController
@RequestMapping("/receipt-item-requests")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptItemRequestController {

	private ReceiptItemRequestService receiptItemRequestService;
	
	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_PURCHASE_STAFF","ROLE_WMS_PURCHASE_MANAGER"})
	@GetMapping
	public ResponseEntity<List<ReceiptItemRequestResponse>> getAllReceiptItemRequests(@RequestParam UUID receiptId) {
		List<ReceiptItemRequestResponse> receiptItems = receiptItemRequestService.getAllReceiptItemRequests(receiptId);
		return ResponseEntity.ok(receiptItems);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}/bays")
	public ResponseEntity<List<BayResponse>> getBaysByReceiptItemRequestId(@PathVariable UUID id) {
		return ResponseEntity.ok(receiptItemRequestService.getBaysByReceiptItemRequestId(id));
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}/general-info")
	public ResponseEntity<ReceiptItemRequestDetailResponse> getReceiptItemRequestDetail(@PathVariable UUID id) {
		return receiptItemRequestService.getReceiptItemRequestDetail(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
