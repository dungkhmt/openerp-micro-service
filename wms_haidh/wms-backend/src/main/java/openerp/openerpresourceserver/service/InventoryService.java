package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import openerp.openerpresourceserver.dto.response.BayResponse;
import openerp.openerpresourceserver.dto.response.InventoryItemResponse;
import openerp.openerpresourceserver.dto.response.LotIdResponse;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.InventoryItemDetail;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.repository.InventoryItemDetailRepository;
import openerp.openerpresourceserver.repository.InventoryItemRepository;
import openerp.openerpresourceserver.repository.WarehouseRepository;

@Service
public class InventoryService {
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private InventoryItemDetailRepository inventoryItemDetailRepository;
	@Autowired
	private WarehouseRepository warehouseRepository;
	@Autowired
	private BayService bayService;

	public void increaseInventory(UUID productId, int quantity, UUID bayId, String lotId, double importPrice,
			LocalDateTime expiredDate) {
		// Tìm InventoryItem dựa trên productId, lotId, bayId
		Optional<InventoryItem> existingItem = inventoryItemRepository.findByProductIdAndLotIdAndBayId(productId, lotId,
				bayId);

		InventoryItem inventoryItem;
		LocalDateTime now = LocalDateTime.now();
		if (existingItem.isPresent()) {
			// Cập nhật số lượng tồn kho nếu đã tồn tại
			inventoryItem = existingItem.get();
			inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() + quantity);
			inventoryItem.setQuantityOnHandTotal(inventoryItem.getQuantityOnHandTotal() + quantity);
			inventoryItem.setLastUpdatedStamp(LocalDateTime.now());
		} else {
			// Tạo mới InventoryItem
			UUID warehouseId = bayService.getWarehouseIdByBayId(bayId);
			inventoryItem = InventoryItem.builder().productId(productId).bayId(bayId).warehouseId(warehouseId)
					.lotId(lotId).availableQuantity(quantity).quantityOnHandTotal(quantity).importPrice(importPrice)
					.currencyUomId("VND").expireDate(expiredDate).createdStamp(now).lastUpdatedStamp(now).build();
		}

		// Lưu InventoryItem
		inventoryItem = inventoryItemRepository.save(inventoryItem);

		// Tạo và lưu InventoryItemDetail
		InventoryItemDetail inventoryItemDetail = InventoryItemDetail.builder()
				.inventoryItemId(inventoryItem.getInventoryItemId()).quantityOnHandDiff(quantity).effectiveDate(now)
				.build();

		inventoryItemDetailRepository.save(inventoryItemDetail);
	}
	
	public void decreaseInventory(AssignedOrderItem item) {

		InventoryItem inventoryItem = inventoryItemRepository.findById(item.getInventoryItemId()).orElseThrow(
				() -> new EntityNotFoundException("InventoryItem not found: " + item.getInventoryItemId()));

		// Update inventory total quantity 
		LocalDateTime now = LocalDateTime.now();
		inventoryItem.setQuantityOnHandTotal(inventoryItem.getQuantityOnHandTotal() - item.getQuantity());
		inventoryItem.setLastUpdatedStamp(now);
		inventoryItemRepository.save(inventoryItem);
		
		// Tạo và lưu InventoryItemDetail
		InventoryItemDetail inventoryItemDetail = InventoryItemDetail.builder()
				.inventoryItemId(inventoryItem.getInventoryItemId()).quantityOnHandDiff(-item.getQuantity()).effectiveDate(now)
				.build();

		inventoryItemDetailRepository.save(inventoryItemDetail);
	}

	public Page<InventoryItemResponse> getInventoryItems(UUID bayId, String lotId, String search, Pageable pageable) {
		if (lotId.equals("all"))
			return inventoryItemRepository.findAllInventoryItems(bayId, search, pageable);
		else
			return inventoryItemRepository.findInventoryItems(bayId, lotId, search, pageable);
	}

	public List<Warehouse> getDistinctWarehousesWithStockBySaleOrderItemId(UUID saleOrderItemId) {
		List<UUID> warehouseIds = inventoryItemRepository
				.findDistinctWarehouseIdsWithStockBySaleOrderItemId(saleOrderItemId);
		return warehouseRepository.findAllById(warehouseIds);
	}

	public List<BayResponse> getBaysWithProductsInSaleOrder(UUID saleOrderItemId, UUID warehouseId) {
		return inventoryItemRepository.findBaysBySaleOrderItemIdAndWarehouseId(saleOrderItemId, warehouseId);
	}

	public List<LotIdResponse> getLotIdsBySaleOrderItemIdAndBayId(UUID saleOrderItemId, UUID bayId) {
		return inventoryItemRepository.findLotIdsBySaleOrderItemIdAndBayId(saleOrderItemId, bayId);
	}

	public Optional<Integer> getAvailableQuantityBySaleOrderItemIdBayIdAndLotId(UUID saleOrderItemId, UUID bayId,
			String lotId) {
		return inventoryItemRepository.findAvailableQuantityBySaleOrderItemIdBayIdAndLotId(saleOrderItemId, bayId,
				lotId);
	}

	public List<String> getDistinctLotIdsByBayId(UUID bayId) {
		return inventoryItemRepository.findDistinctLotIdsByBayId(bayId);
	}
}
