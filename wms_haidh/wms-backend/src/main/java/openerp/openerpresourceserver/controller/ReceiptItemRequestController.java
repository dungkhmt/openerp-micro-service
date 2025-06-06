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
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.projection.ReceiptItemRequestDetailProjection;
import openerp.openerpresourceserver.projection.ReceiptItemRequestProjection;
import openerp.openerpresourceserver.service.ReceiptItemRequestService;

@RestController
@RequestMapping("/receipt-item-requests")
@Validated
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ReceiptItemRequestController {

	private ReceiptItemRequestService receiptItemRequestService;
	
	@Secured({"ROLE_WMS_WAREHOUSE_MANAGER","ROLE_WMS_PURCHASE_STAFF","ROLE_WMS_PURCHASE_MANAGER"})
	@GetMapping
	public ResponseEntity<List<ReceiptItemRequestProjection>> getAllReceiptItemRequests(@RequestParam UUID receiptId) {
		List<ReceiptItemRequestProjection> receiptItems = receiptItemRequestService.getAllReceiptItemRequests(receiptId);
		return ResponseEntity.ok(receiptItems);
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}/bays")
	public ResponseEntity<List<BayProjection>> getBaysByReceiptItemRequestId(@PathVariable UUID id) {
		return ResponseEntity.ok(receiptItemRequestService.getBaysByReceiptItemRequestId(id));
	}

	@Secured("ROLE_WMS_WAREHOUSE_MANAGER")
	@GetMapping("/{id}/general-info")
	public ResponseEntity<ReceiptItemRequestDetailProjection> getReceiptItemRequestDetail(@PathVariable UUID id) {
		return receiptItemRequestService.getReceiptItemRequestDetail(id).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
