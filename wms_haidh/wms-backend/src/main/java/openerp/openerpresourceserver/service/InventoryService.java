package openerp.openerpresourceserver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.InventoryItemDetail;
import openerp.openerpresourceserver.entity.projection.InventoryItemProjection;
import openerp.openerpresourceserver.repository.InventoryItemDetailRepository;
import openerp.openerpresourceserver.repository.InventoryItemRepository;

@Service
public class InventoryService {
	@Autowired
    private InventoryItemRepository inventoryItemRepository;
	@Autowired
    private InventoryItemDetailRepository inventoryItemDetailRepository;
    @Autowired
    private BayService bayService;

    public void updateInventory(UUID productId, int quantity, UUID bayId, String lotId, BigDecimal importPrice, LocalDateTime expiredDate) {
        // Tìm InventoryItem dựa trên productId, lotId, bayId
        Optional<InventoryItem> existingItem = inventoryItemRepository.findByProductIdAndLotIdAndBayId(productId, lotId, bayId);

        InventoryItem inventoryItem;
        LocalDateTime now = LocalDateTime.now();
        if (existingItem.isPresent()) {
            // Cập nhật số lượng tồn kho nếu đã tồn tại
            inventoryItem = existingItem.get();
            inventoryItem.setQuantityOnHandTotal(inventoryItem.getQuantityOnHandTotal() + quantity);
            inventoryItem.setLastUpdatedStamp(LocalDateTime.now());
        } else {
            // Tạo mới InventoryItem
        	UUID warehouseId = bayService.getWarehouseIdByBayId(bayId);
            inventoryItem = InventoryItem.builder()
                    .productId(productId)
                    .bayId(bayId)
                    .warehouseId(warehouseId)
                    .lotId(lotId)
                    .quantityOnHandTotal(quantity)
                    .importPrice(importPrice)
                    .currencyUomId("VND")
                    .datetimeReceived(now)
                    .expireDate(expiredDate)
                    .createdStamp(now)
                    .lastUpdatedStamp(now)
                    .build();
        }

        // Lưu InventoryItem
        inventoryItem = inventoryItemRepository.save(inventoryItem);

        // Tạo và lưu InventoryItemDetail
        InventoryItemDetail inventoryItemDetail = InventoryItemDetail.builder()
                .inventoryItemId(inventoryItem.getInventoryItemId())
                .quantityOnHandDiff(quantity)
                .effectiveDate(now)
                .build();

        inventoryItemDetailRepository.save(inventoryItemDetail);
    }
    
    public Page<InventoryItemProjection> getInventoryItems(UUID warehouseId, UUID bayId, Pageable pageable) {
        if (bayId != null) {
            return inventoryItemRepository.findInventoryItemsByBay(bayId, pageable);
        } else if (warehouseId != null) {
            return inventoryItemRepository.findInventoryItemsByWarehouse(warehouseId, pageable);
        } else {
            return inventoryItemRepository.findAllInventoryItems(pageable);
        }
    }
}
