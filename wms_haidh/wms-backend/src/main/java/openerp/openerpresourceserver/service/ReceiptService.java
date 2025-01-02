package openerp.openerpresourceserver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.entity.ReceiptItemRequest;
import openerp.openerpresourceserver.entity.projection.ReceiptInfoProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemDetailProjection;
import openerp.openerpresourceserver.model.request.ReceiptCreate;
import openerp.openerpresourceserver.model.request.ReceiptItemRequestCreate;
import openerp.openerpresourceserver.repository.ReceiptItemRequestRepository;
import openerp.openerpresourceserver.repository.ReceiptRepository;

@Service
public class ReceiptService {

	@Autowired
	private ReceiptRepository receiptRepository;
	@Autowired
	private ReceiptItemRequestRepository receiptItemRequestRepository;
	

	public Page<ReceiptInfoProjection> searchReceipts(String status, Pageable pageable) {
		return receiptRepository.findReceiptsByStatus(status, pageable);
	}

	public Receipt createReceipt(ReceiptCreate request) {

		if (request.getCreatedBy() == null || request.getCreatedBy().isEmpty()) {
			throw new IllegalArgumentException("CreatedBy cannot be empty");
		}

		// Set timestamps
		LocalDateTime now = LocalDateTime.now();

		// Convert LocalDate to LocalDateTime
		LocalDateTime receiptDateTime = request.getReceiptDate() != null ? request.getReceiptDate().atStartOfDay()
				: null;
		LocalDateTime expectedReceiptDateTime = request.getExpectedReceiptDate() != null
				? request.getExpectedReceiptDate().atStartOfDay()
				: null;

		// Build receipt object
		Receipt receipt = Receipt.builder().receiptName(request.getReceiptName()).description(request.getDescription())
				.receiptDate(receiptDateTime) // Converted to LocalDateTime
				.warehouseId(request.getWarehouseId()).createdReason(request.getCreatedReason())
				.expectedReceiptDate(expectedReceiptDateTime) // Converted to LocalDateTime
				.status("CREATED").createdBy(request.getCreatedBy()).createdStamp(now).lastUpdatedStamp(now) 																						
				.build();

		// Save to database
		return receiptRepository.save(receipt);
	}

	public List<ReceiptItemDetailProjection> getReceiptItemDetails(UUID receiptId) {
		return receiptItemRequestRepository.findReceiptItemDetails(receiptId);
	}

	public boolean approveReceipt(UUID receiptId, String approvedBy) {
		Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);

		if (receiptOptional.isPresent()) {
			Receipt receipt = receiptOptional.get();
			receipt.setStatus("APPROVED");
			receipt.setLastUpdatedStamp(LocalDateTime.now());
			// Thêm thông tin người duyệt
			receipt.setApprovedBy(approvedBy);
			receiptRepository.save(receipt);
			return true;
		}

		return false; // Nếu không tìm thấy receipt
	}

	public boolean cancelReceipt(UUID receiptId, String cancelledBy) {
		Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);

		if (receiptOptional.isPresent()) {
			Receipt receipt = receiptOptional.get();
			receipt.setStatus("CANCELLED");
			receipt.setLastUpdatedStamp(LocalDateTime.now());
			// Thêm thông tin người từ chối
			receipt.setCancelledBy(cancelledBy);
			receiptRepository.save(receipt);
			return true;
		}

		return false; // Nếu không tìm thấy receipt
	}

	public void createReceiptItems(UUID receiptId, List<ReceiptItemRequestCreate> receiptItemRequests) {
		LocalDateTime now = LocalDateTime.now();
		// Tạo mới ReceiptItemRequest cho từng item
		for (ReceiptItemRequestCreate item : receiptItemRequests) {
			ReceiptItemRequest receiptItemRequest = new ReceiptItemRequest();
			receiptItemRequest.setReceiptId(receiptId);
			receiptItemRequest.setProductId(item.getProductId());
			receiptItemRequest.setQuantity(item.getQuantity());
			receiptItemRequest.setWarehouseId(item.getWarehouseId());
			receiptItemRequest.setCompleted(new BigDecimal(0));
			receiptItemRequest.setLastUpdated(now);
			// Lưu ReceiptItemRequest vào DB
			receiptItemRequestRepository.save(receiptItemRequest);
		}

	}

	

	

}
