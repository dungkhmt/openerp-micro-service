package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.dto.request.ReceiptItemCreateRequest;
import openerp.openerpresourceserver.dto.response.PutawayItemResponse;
import openerp.openerpresourceserver.dto.response.ReceiptItemResponse;
import openerp.openerpresourceserver.entity.Receipt;
import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.repository.ReceiptItemRepository;
import openerp.openerpresourceserver.repository.ReceiptRepository;

@Service
public class ReceiptItemService {

	@Autowired
	private ReceiptItemRepository repository;
	@Autowired
	private ReceiptRepository receiptRepository;
	@Autowired
	private ReceiptItemRequestService receiptItemRequestService;
	@Autowired
	private InventoryService inventoryService;

	public List<ReceiptItemResponse> getItemsByRequestId(UUID receiptItemRequestId) {
		return repository.findByReceiptItemRequestId(receiptItemRequestId);
	}

	public ReceiptItem createReceiptItem(ReceiptItemCreateRequest request) {
	    // Lấy thông tin cần thiết từ ReceiptItemRequest
	    UUID receiptId = receiptItemRequestService.getReceiptIdByRequestId(request.getReceiptItemRequestId());
	    UUID productId = receiptItemRequestService.getProductIdByRequestId(request.getReceiptItemRequestId());
	    LocalDateTime expiredDate = request.getExpiredDate() != null ? request.getExpiredDate().atStartOfDay() : null;

	    // Tạo đối tượng ReceiptItem
	    ReceiptItem receiptItem = ReceiptItem.builder()
	            .productId(productId)
	            .quantity(request.getQuantity())
	            .bayId(request.getBayId())
	            .lotId(request.getLotId())
	            .importPrice(request.getImportPrice())
	            .status("CREATED")            
	            .expiredDate(expiredDate)
	            .receiptItemRequestId(request.getReceiptItemRequestId())
	            .receiptId(receiptId)
	            .createdStamp(LocalDateTime.now())
	            .lastUpdatedStamp(LocalDateTime.now())
	            .build();

	    // Lưu ReceiptItem
	    ReceiptItem savedReceiptItem = repository.save(receiptItem);

	    return savedReceiptItem;
	}

	public Page<PutawayItemResponse> getPutawayItems(UUID bayId, String status, Pageable pageable) {
		return repository.findPutawayItems(bayId, status, pageable);
	}

	public void markAsPutawayed(UUID receiptItemId) {
		ReceiptItem item = repository.findById(receiptItemId)
				.orElseThrow(() -> new EntityNotFoundException("ReceiptItem not found: " + receiptItemId));

		if (!item.getStatus().equals("CREATED"))
			throw new RuntimeException("Only item with status 'CREATED' can be putawayed.");
		item.setStatus("PUTAWAYED");
		item.setLastUpdatedStamp(LocalDateTime.now());
		repository.save(item);
		
		// Cập nhật InventoryItem và InventoryItemDetail
	    inventoryService.increaseInventory(
	            item.getProductId(),
	            item.getQuantity(),
	            item.getBayId(),
	            item.getLotId(),
	            item.getImportPrice(),
	            item.getExpiredDate()
	    );

		UUID receiptId = item.getReceiptId();

		// Kiểm tra trạng thái đơn hàng
		Receipt receipt = receiptRepository.findById(receiptId)
				.orElseThrow(() -> new EntityNotFoundException("Receipt not found: " + receiptId));

		if ("ASSIGNED".equals(receipt.getStatus())) {
			boolean allPutawayed = repository.countByReceiptIdAndStatusNot(receiptId, "PUTAWAYED") == 0;

			if (allPutawayed) {
				receipt.setStatus("PUTAWAY_COMPLETE");
				receipt.setLastUpdatedStamp(LocalDateTime.now());
				receiptRepository.save(receipt);
			}
		}
		
	}

}
