package openerp.openerpresourceserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.request.AssignedOrderItemCreateRequest;
import openerp.openerpresourceserver.dto.request.Item;
import openerp.openerpresourceserver.dto.response.AssignedOrderItemResponse;
import openerp.openerpresourceserver.dto.response.DeliveryOrderItemResponse;
import openerp.openerpresourceserver.dto.response.PickedOrderItemResponse;
import openerp.openerpresourceserver.entity.AssignedOrderItem;
import openerp.openerpresourceserver.entity.InventoryItem;
import openerp.openerpresourceserver.entity.Order;
import openerp.openerpresourceserver.entity.SaleOrderItem;
import openerp.openerpresourceserver.repository.AssignedOrderItemRepository;
import openerp.openerpresourceserver.repository.InventoryItemRepository;
import openerp.openerpresourceserver.repository.OrderRepository;
import openerp.openerpresourceserver.repository.SaleOrderItemRepository;

@Service
public class AssignedOrderItemService {

	@Autowired
	private AssignedOrderItemRepository assignedOrderItemRepository;
	@Autowired
	private SaleOrderItemRepository saleOrderItemRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private InventoryItemRepository inventoryItemRepository;
	@Autowired
	private InventoryService inventoryService;

	public List<AssignedOrderItemResponse> getAssignedOrderItemsBySaleOrderItemId(UUID saleOrderItemId) {
		return assignedOrderItemRepository.findAssignedOrderItemsBySaleOrderItemId(saleOrderItemId);
	}

	public Page<PickedOrderItemResponse> getAllPickedOrderItems(UUID bayId, String status, Pageable pageable) {
		return assignedOrderItemRepository.findAllPickedOrderItemsByBay(bayId, status, pageable);
	}

	public Page<DeliveryOrderItemResponse> getAllDeliveryOrderItems(UUID warehouseId, Pageable pageable) {
		return assignedOrderItemRepository.findAllDeliveryOrderItemsByWarehouse(warehouseId, pageable);
	}

	public AssignedOrderItem assignOrderItem(AssignedOrderItemCreateRequest dto, String userLoginId) {
		// Step 1: Retrieve SaleOrderItem
		SaleOrderItem saleOrderItem = saleOrderItemRepository.findById(dto.getSaleOrderItemId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid SaleOrderItemId"));
		Order order = orderRepository.findById(saleOrderItem.getOrderId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid OrderId"));

		if (!"APPROVED".equals(order.getStatus()) && !"IN_PROGRESS".equals(order.getStatus())) {
			throw new IllegalStateException("This order can not be assigned.");
		}
		// Step 2: Retrieve productId from SaleOrderItem
		UUID productId = saleOrderItem.getProductId();

		// Step 3: Retrieve InventoryItem
		InventoryItem inventoryItem = inventoryItemRepository
				.findByProductIdAndLotIdAndBayId(productId, dto.getLotId(), dto.getBayId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid InventoryItem for given parameters"));

		// Step 4: Verify sufficient quantity in inventory
		if (inventoryItem.getAvailableQuantity() < dto.getQuantity()) {
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
		assignedOrderItem.setAssignedBy(userLoginId);
		assignedOrderItem.setLastUpdatedStamp(LocalDateTime.now());
		assignedOrderItem.setCreatedStamp(LocalDateTime.now());
		assignedOrderItem.setStatus("CREATED");
		assignedOrderItem.setInventoryItemId(inventoryItem.getInventoryItemId());

		// Save the AssignedOrderItem
		assignedOrderItemRepository.save(assignedOrderItem);
		
		// Update available inventory quantity
		inventoryItem.setAvailableQuantity(inventoryItem.getAvailableQuantity() - dto.getQuantity());
		inventoryItemRepository.save(inventoryItem);

		return assignedOrderItem;
	}

	@Transactional
	public int updateAssignedOrderItemsStatus(List<UUID> assignedOrderItemIds, String status) {
		return assignedOrderItemRepository.updateStatusByIds(assignedOrderItemIds, status, LocalDateTime.now());
	}

	public List<Item> getItems() {
		return assignedOrderItemRepository.getAllItems();
	}

	public List<DeliveryOrderItemResponse> getDeliveryOrderItems(List<UUID> assignedOrderItemIds) {
		return assignedOrderItemRepository.findDeliveryOrderItemsByIds(assignedOrderItemIds);
	}

	public long countAssignedItems(UUID orderId) {
		return assignedOrderItemRepository.countAssignedItems(orderId);
	}

	public void markAsPicked(UUID assignedOrderItemId) {
		AssignedOrderItem item = assignedOrderItemRepository.findById(assignedOrderItemId)
				.orElseThrow(() -> new EntityNotFoundException("AssignedOrderItem not found: " + assignedOrderItemId));

		if (!item.getStatus().equals("CREATED"))
			throw new RuntimeException("Only item with status 'CREATED' can be picked.");
		item.setStatus("PICKED");
		item.setLastUpdatedStamp(LocalDateTime.now());
		assignedOrderItemRepository.save(item);

		// Cập nhật InventoryItem và InventoryItemDetail
		inventoryService.decreaseInventory(item);
		
		UUID orderId = item.getOrderId();

		// Kiểm tra trạng thái đơn hàng
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

		if ("ASSIGNED".equals(order.getStatus())) {
			boolean allPicked = assignedOrderItemRepository.countByOrderIdAndStatusNot(orderId, "PICKED") == 0;

			if (allPicked) {
				order.setStatus("PICK_COMPLETE");
				order.setLastUpdatedStamp(LocalDateTime.now());
				orderRepository.save(order);
			}
		}
	}

}
