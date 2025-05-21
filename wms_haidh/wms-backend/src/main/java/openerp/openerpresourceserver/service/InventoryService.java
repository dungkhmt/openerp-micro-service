package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.InventoryItemDetail;
import openerp.openerpresourceserver.entity.Warehouse;
import openerp.openerpresourceserver.projection.BayProjection;
import openerp.openerpresourceserver.projection.InventoryItemProjection;
import openerp.openerpresourceserver.projection.LotIdProjection;
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

	public void updateInventory(UUID productId, int quantity, UUID bayId, String lotId, double importPrice,
			LocalDateTime expiredDate) {
		// Tìm InventoryItem dựa trên productId, lotId, bayId
		Optional<InventoryItem> existingItem = inventoryItemRepository.findByProductIdAndLotIdAndBayId(productId, lotId,
				bayId);

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
			inventoryItem = InventoryItem.builder().productId(productId).bayId(bayId).warehouseId(warehouseId)
					.lotId(lotId).quantityOnHandTotal(quantity).importPrice(importPrice).currencyUomId("VND")
					.expireDate(expiredDate).createdStamp(now).lastUpdatedStamp(now).build();
		}

		// Lưu InventoryItem
		inventoryItem = inventoryItemRepository.save(inventoryItem);

		// Tạo và lưu InventoryItemDetail
		InventoryItemDetail inventoryItemDetail = InventoryItemDetail.builder()
				.inventoryItemId(inventoryItem.getInventoryItemId()).quantityOnHandDiff(quantity).effectiveDate(now)
				.build();

		inventoryItemDetailRepository.save(inventoryItemDetail);
	}

	public Page<InventoryItemProjection> getInventoryItems(UUID bayId, String lotId, String search, Pageable pageable) {
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

	public List<BayProjection> getBaysWithProductsInSaleOrder(UUID saleOrderItemId, UUID warehouseId) {
		return inventoryItemRepository.findBayProjectionsBySaleOrderItemIdAndWarehouseId(saleOrderItemId, warehouseId);
	}

	public List<LotIdProjection> getLotIdsBySaleOrderItemIdAndBayId(UUID saleOrderItemId, UUID bayId) {
		return inventoryItemRepository.findLotIdsBySaleOrderItemIdAndBayId(saleOrderItemId, bayId);
	}

	public Optional<Integer> getQuantityOnHandBySaleOrderItemIdBayIdAndLotId(UUID saleOrderItemId, UUID bayId,
			String lotId) {
		return inventoryItemRepository.findQuantityOnHandBySaleOrderItemIdBayIdAndLotId(saleOrderItemId, bayId, lotId);
	}

	public List<String> getDistinctLotIdsByBayId(UUID bayId) {
		return inventoryItemRepository.findDistinctLotIdsByBayId(bayId);
	}
}
