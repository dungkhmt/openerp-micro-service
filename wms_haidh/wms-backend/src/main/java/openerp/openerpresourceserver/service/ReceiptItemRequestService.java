package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.dto.request.ReceiptItemRequestDTO;
import openerp.openerpresourceserver.entity.ReceiptItemRequest;
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.projection.ReceiptItemRequestDetailProjection;
import openerp.openerpresourceserver.projection.ReceiptItemRequestProjection;
import openerp.openerpresourceserver.repository.ReceiptItemRequestRepository;

@Service
public class ReceiptItemRequestService {

	@Autowired
    private ReceiptItemRequestRepository receiptItemRequestRepository;
	@Autowired
	private BayService bayService;

	public UUID getWarehouseIdByReceiptItemRequestId(UUID receiptItemRequestId) {
		return receiptItemRequestRepository.findByReceiptItemRequestId(receiptItemRequestId)
				.map(ReceiptItemRequest::getWarehouseId).orElseThrow(() -> new EntityNotFoundException(
						"ReceiptItemRequest not found for ID: " + receiptItemRequestId));
	}
	
	public List<BayProjection> getBaysByReceiptItemRequestId(UUID receiptItemRequestId) {
		UUID warehouseId = getWarehouseIdByReceiptItemRequestId(receiptItemRequestId);
		return bayService.getBaysProjectionByWarehouseId(warehouseId);
	}
    
    public List<ReceiptItemRequestProjection> getAllReceiptItemRequests(UUID receiptId) {
        return receiptItemRequestRepository.findAllWithDetails(receiptId);
    }
    
    public Optional<ReceiptItemRequestDetailProjection> getReceiptItemRequestDetail(UUID receiptItemRequestId) {
        return receiptItemRequestRepository.findDetailById(receiptItemRequestId);
    }
    
    public UUID getReceiptIdByRequestId(UUID receiptItemRequestId) {
        return receiptItemRequestRepository.findById(receiptItemRequestId)
                .map(ReceiptItemRequest::getReceiptId)
                .orElseThrow(() -> new IllegalArgumentException("Receipt Item Request ID not found: " + receiptItemRequestId));
    }
    public UUID getProductIdByRequestId(UUID receiptItemRequestId) {
        return receiptItemRequestRepository.findById(receiptItemRequestId)
                .map(ReceiptItemRequest::getProductId)
                .orElseThrow(() -> new IllegalArgumentException("Receipt Item Request ID not found: " + receiptItemRequestId));
    }
    
	public void createReceiptItems(UUID receiptId, List<ReceiptItemRequestDTO> receiptItemRequests) {
		LocalDateTime now = LocalDateTime.now();
		// Tạo mới ReceiptItemRequest cho từng item
		for (ReceiptItemRequestDTO item : receiptItemRequests) {
			ReceiptItemRequest receiptItemRequest = new ReceiptItemRequest();
			receiptItemRequest.setReceiptId(receiptId);
			receiptItemRequest.setProductId(item.getProductId());
			receiptItemRequest.setQuantity(item.getQuantity());
			receiptItemRequest.setWarehouseId(item.getWarehouseId());
			receiptItemRequest.setCompleted(0);
			receiptItemRequest.setLastUpdated(now);
			// Lưu ReceiptItemRequest vào DB
			receiptItemRequestRepository.save(receiptItemRequest);
		}

	}
}
