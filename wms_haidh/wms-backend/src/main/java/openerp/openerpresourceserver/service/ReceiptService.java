package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.ReceiptCreateRequest;
import openerp.openerpresourceserver.dto.response.ReceiptInfoResponse;
import openerp.openerpresourceserver.dto.response.ReceiptResponse;
import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.repository.ReceiptRepository;

@Service
public class ReceiptService {

	@Autowired
	private ReceiptRepository receiptRepository;

	public Optional<ReceiptResponse> getReceiptDetailsById(UUID id) {
        return receiptRepository.findReceiptDetailsById(id);
    }

	public Page<ReceiptInfoResponse> searchReceipts(String status, Pageable pageable) {
		return receiptRepository.findReceiptsByStatus(status, pageable);
	}

	public Receipt createReceipt(ReceiptCreateRequest request, String userLoginId) {

		// Set timestamps
		LocalDateTime now = LocalDateTime.now();

		LocalDateTime expectedReceiptDateTime = request.getExpectedReceiptDate() != null
				? request.getExpectedReceiptDate().atStartOfDay()
				: null;

		// Build receipt object
		Receipt receipt = Receipt.builder().receiptName(request.getReceiptName()).description(request.getDescription())
				.warehouseId(request.getWarehouseId())
				.supplierId(request.getSupplierId())
				.expectedReceiptDate(expectedReceiptDateTime) // Converted to LocalDateTime
				.status("CREATED").createdBy(userLoginId).createdStamp(now).lastUpdatedStamp(now).build();

		// Save to database
		return receiptRepository.save(receipt);
	}

	public boolean approveReceipt(UUID receiptId, String approvedBy) {
		Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);

		if (receiptOptional.isPresent()) {
			Receipt receipt = receiptOptional.get();
			if (!"CREATED".equals(receipt.getStatus())) {
				return false;
			}
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
			if (!"CREATED".equals(receipt.getStatus())) {
				return false;
			}
			receipt.setStatus("CANCELLED");
			receipt.setLastUpdatedStamp(LocalDateTime.now());
			// Thêm thông tin người từ chối
			receipt.setCancelledBy(cancelledBy);
			receiptRepository.save(receipt);
			return true;
		}

		return false; // Nếu không tìm thấy receipt
	}

}
