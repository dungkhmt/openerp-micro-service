package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.dto.request.ReceiptItemCreateRequest;
import openerp.openerpresourceserver.entity.ReceiptItem;
import openerp.openerpresourceserver.projection.ReceiptItemProjection;
import openerp.openerpresourceserver.repository.ReceiptItemRepository;

@Service
public class ReceiptItemService {

	@Autowired
	private ReceiptItemRepository repository;
	@Autowired
	private ReceiptItemRequestService receiptItemRequestService;
	@Autowired
	private ReceiptBillService receiptBillService;
	@Autowired
	private InventoryService inventoryService;

	public List<ReceiptItemProjection> getItemsByRequestId(UUID receiptItemRequestId) {
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
	            .expiredDate(expiredDate)
	            .receiptItemRequestId(request.getReceiptItemRequestId())
	            .receiptBillId(request.getReceiptBillId())
	            .receiptId(receiptId)
	            .createdStamp(LocalDateTime.now())
	            .lastUpdatedStamp(LocalDateTime.now())
	            .build();

	    // Lưu ReceiptItem
	    ReceiptItem savedReceiptItem = repository.save(receiptItem);

	    // Cập nhật InventoryItem và InventoryItemDetail
	    inventoryService.updateInventory(
	            productId,
	            request.getQuantity(),
	            request.getBayId(),
	            request.getLotId(),
	            request.getImportPrice(),
	            expiredDate
	    );

	    // Cập nhật tổng giá trị của ReceiptBill
	    receiptBillService.updateTotalPrice(request.getImportPrice(), request.getQuantity(), request.getReceiptBillId());

	    return savedReceiptItem;
	}

}
