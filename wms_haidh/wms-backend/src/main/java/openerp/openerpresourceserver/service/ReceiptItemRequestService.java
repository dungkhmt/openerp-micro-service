package openerp.openerpresourceserver.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.entity.ReceiptItemRequest;
import openerp.openerpresourceserver.entity.projection.BayProjection;
import openerp.openerpresourceserver.entity.projection.ReceiptItemRequestProjection;
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
		return bayService.getBaysByWarehouseId(warehouseId);
	}
    
    public Page<ReceiptItemRequestProjection> getAllReceiptItemRequests(String status,Pageable pageable) {
        return receiptItemRequestRepository.findAllWithDetails(status,pageable);
    }
    
    public Optional<ReceiptItemRequestProjection> getReceiptItemRequestDetail(UUID receiptItemRequestId) {
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
}
