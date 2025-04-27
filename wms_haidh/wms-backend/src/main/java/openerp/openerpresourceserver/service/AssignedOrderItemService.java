package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.request.AssignedOrderItemCreateRequest;
import openerp.openerpresourceserver.dto.request.Item;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.SaleOrderItem;
import openerp.openerpresourceserver.projection.AssignedOrderItemProjection;
import openerp.openerpresourceserver.projection.DeliveryOrderItemProjection;
import openerp.openerpresourceserver.repository.AssignedOrderItemRepository;
import openerp.openerpresourceserver.repository.InventoryItemRepository;
import openerp.openerpresourceserver.repository.SaleOrderItemRepository;

@Service
public class AssignedOrderItemService {

	@Autowired
	private AssignedOrderItemRepository assignedOrderItemRepository;
	@Autowired
	private SaleOrderItemRepository saleOrderItemRepository;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	public List<AssignedOrderItemProjection> getAssignedOrderItemsBySaleOrderItemId(UUID saleOrderItemId) {
		return assignedOrderItemRepository.findAssignedOrderItemsBySaleOrderItemId(saleOrderItemId);
	}
	
	public Page<DeliveryOrderItemProjection> getAllDeliveryOrderItems(UUID warehouseId,Pageable pageable) {
        return assignedOrderItemRepository.findAllDeliveryOrderItemsByWarehouse(warehouseId,pageable);
    }

	public AssignedOrderItem assignOrderItem(AssignedOrderItemCreateRequest dto) {
        // Step 1: Retrieve SaleOrderItem
        SaleOrderItem saleOrderItem = saleOrderItemRepository.findById(dto.getSaleOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid SaleOrderItemId"));

        // Step 2: Retrieve productId from SaleOrderItem
        UUID productId = saleOrderItem.getProductId();

        // Step 3: Retrieve InventoryItem
        InventoryItem inventoryItem = inventoryItemRepository.findByProductIdAndLotIdAndBayId(productId, dto.getLotId(), dto.getBayId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid InventoryItem for given parameters"));

        // Step 4: Verify sufficient quantity in inventory
        if (inventoryItem.getQuantityOnHandTotal() < dto.getQuantity()) {
            throw new IllegalArgumentException("Insufficient quantity in inventory");
        }
        
        // Create a new AssignedOrderItem
        AssignedOrderItem assignedOrderItem = new AssignedOrderItem();
        assignedOrderItem.setOrderId(saleOrderItem.getOrderId()); 
        assignedOrderItem.setProductId(productId);
        assignedOrderItem.setQuantity(dto.getQuantity());
        assignedOrderItem.setBayId(dto.getBayId());
        assignedOrderItem.setWarehouseId(dto.getWarehouseId());
        assignedOrderItem.setLotId(dto.getLotId());
        assignedOrderItem.setAssignedBy(dto.getAssignedBy());
        assignedOrderItem.setLastUpdatedStamp(LocalDateTime.now());
        assignedOrderItem.setCreatedStamp(LocalDateTime.now());
        assignedOrderItem.setStatus("CREATED");
        assignedOrderItem.setInventoryItemId(inventoryItem.getInventoryItemId());
        assignedOrderItem.setOriginalQuantity(saleOrderItem.getQuantity());

        // Save the AssignedOrderItem
        assignedOrderItemRepository.save(assignedOrderItem);

        // Update inventory quantity
        inventoryItem.setQuantityOnHandTotal(inventoryItem.getQuantityOnHandTotal() - dto.getQuantity());
        inventoryItemRepository.save(inventoryItem);
        
//        orderService.distributeOrder(saleOrderItem.getOrderId());

        return assignedOrderItem;
    }
	
	@Transactional
	public int updateAssignedOrderItemsStatus(List<UUID> assignedOrderItemIds, String status) {
	    return assignedOrderItemRepository.updateStatusByIds(assignedOrderItemIds, status, LocalDateTime.now());
	}
	
	public List<Item> getItems() {
        return assignedOrderItemRepository.getAllItems();
    }
	
	public List<DeliveryOrderItemProjection> getDeliveryOrderItems(List<UUID> assignedOrderItemIds) {
        return assignedOrderItemRepository.findDeliveryOrderItemsByIds(assignedOrderItemIds);
    }

}
