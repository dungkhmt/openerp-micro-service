package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.entity.enumentity.AssignedOrderItemStatus;
import com.hust.wmsbackend.management.entity.enumentity.DeliveryTripItemStatus;
import com.hust.wmsbackend.management.model.AssignedOrderItemDTO;
import com.hust.wmsbackend.management.model.request.DeliveryTripItemSuggestRequest;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.AssignedOrderItemService;
import com.hust.wmsbackend.management.service.CustomerAddressService;
import com.hust.wmsbackend.management.service.DeliveryTripItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DeliveryTripItemServiceImpl implements DeliveryTripItemService {

    private DeliveryTripItemRepository deliveryTripItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private InventoryItemDetailRepository inventoryItemDetailRepository;
    private CustomerAddressRepository customerAddressRepository;
    private SaleOrderHeaderRepository saleOrderHeaderRepository;

    private AssignedOrderItemService assignedOrderItemService;
    private CustomerAddressService customerAddressService;

    @Override
    public boolean updateItemStatus(String itemId, String newStatusCodeStr) {
        Optional<DeliveryTripItem> itemOpt = deliveryTripItemRepository.findById(itemId);
        if (!itemOpt.isPresent()) {
            log.warn(String.format("Delivery trip item with id %s is not exist", itemId));
            return false;
        }
        DeliveryTripItem item = itemOpt.get();
        DeliveryTripItemStatus newStatus = DeliveryTripItemStatus.findByCode(newStatusCodeStr);
        if (newStatus == null) {
            log.warn(String.format("New status for %s not found", newStatusCodeStr));
            return false;
        }
        item.setStatus(newStatus);
        deliveryTripItemRepository.save(item);
        return true;
    }

    @Override
    @Transactional
    public boolean complete(String[] itemIds) {
        List<String> itemIdList = Arrays.asList(itemIds);
        if (itemIdList.isEmpty()) {
            log.info("Item id list to update complete is empty");
            return false;
        }
        List<DeliveryTripItem> updateItems = new ArrayList<>();
        List<InventoryItemDetail> itemDetails = new ArrayList<>();
        for (String itemId : itemIdList) {
            DeliveryTripItem item = getByIdOrThrow(itemId);
            if (item.getStatus() == DeliveryTripItemStatus.CREATED) {
                throw new RuntimeException("Delivery trip item is CREATED status");
            }
            item.setStatus(DeliveryTripItemStatus.DONE);
            updateItems.add(item);

            Optional<AssignedOrderItem> assignedOrderItemOpt = assignedOrderItemRepository.findById(item.getAssignedOrderItemId());
            assignedOrderItemOpt.ifPresent(assignedOrderItem -> itemDetails.add(InventoryItemDetail.builder()
                    .inventoryItemId(assignedOrderItem.getInventoryItemId())
                    .inventoryItemDetailId(UUID.randomUUID())
                    .effectiveDate(new Date())
                    .quantityOnHandDiff(item.getQuantity())
                    .build()));
        }
        deliveryTripItemRepository.saveAll(updateItems);
        inventoryItemDetailRepository.saveAll(itemDetails);
        return true;
    }

    @Override
    public boolean fail(String[] itemIds) {
        List<String> itemIdList = Arrays.asList(itemIds);
        if (itemIdList.isEmpty()) {
            log.info("Item id list to update complete is empty");
            return false;
        }
        List<DeliveryTripItem> updateItems = new ArrayList<>();
        for (String itemId : itemIdList) {
            DeliveryTripItem item = getByIdOrThrow(itemId);
            if (item.getStatus() == DeliveryTripItemStatus.CREATED) {
                throw new RuntimeException("Delivery trip item is CREATED status");
            }
            item.setStatus(DeliveryTripItemStatus.FAIL);
            updateItems.add(item);
        }
        deliveryTripItemRepository.saveAll(updateItems);
        return true;
    }

    @Override
    public List<AssignedOrderItemDTO> getDeliveryTripItemSuggest(DeliveryTripItemSuggestRequest request) {
        if (request.getWarehouseId() == null || request.getWarehouseId().isEmpty()
            || request.getAssignedOrderItemIds() == null || request.getAssignedOrderItemIds().isEmpty()
        ) {
            return assignedOrderItemService.getAllCreatedItems();
        }
        // lấy tất cả assigned_order_item có cùng warehouseId và status = CREATED
        UUID warehouseId = UUID.fromString(request.getWarehouseId());
        List<AssignedOrderItem> assignedOrderItems = assignedOrderItemRepository.findAllByWarehouseIdAndStatus(
                warehouseId, AssignedOrderItemStatus.CREATED);
        if (assignedOrderItems.isEmpty()) {
            return new ArrayList<>();
        }
        // lấy customer_address từ assigned_order_item
        List<CustomerAddress> selectedAddresses = new ArrayList<>();
        List<SaleOrderHeader> orders = new ArrayList<>(); // với mỗi item sẽ tìm được order tương ứng từ orderId
        for (AssignedOrderItem item : assignedOrderItems) {
            Optional<SaleOrderHeader> orderOpt = saleOrderHeaderRepository.findById(item.getOrderId());
            if (!orderOpt.isPresent()) {
                throw new RuntimeException(String.format("Order id %s not found", item.getOrderId()));
            }
            SaleOrderHeader order = orderOpt.get();
            orders.add(order);
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(order.getCustomerAddressId());
            if (!customerAddressOpt.isPresent()) {
                throw new RuntimeException(String.format("Customer address id %s not found", order.getCustomerAddressId()));
            }
            selectedAddresses.add(customerAddressOpt.get());
        }

        // lấy cluster từ danh sách selected assigned_order_item được truyền lên từ request
        List<CustomerAddress> cluster = new ArrayList<>();

        for (String assignedOrderItemIdStr : request.getAssignedOrderItemIds()) {
            Optional<AssignedOrderItem> itemOpt = assignedOrderItemRepository.findById(UUID.fromString(assignedOrderItemIdStr));
            if (!itemOpt.isPresent()) {
                throw new RuntimeException(String.format("Assigned order item id %s not found", assignedOrderItemIdStr));
            }
            AssignedOrderItem item = itemOpt.get();
            Optional<SaleOrderHeader> orderOpt = saleOrderHeaderRepository.findById(item.getOrderId());
            if (!orderOpt.isPresent()) {
                throw new RuntimeException(String.format("Order id %s not found", item.getOrderId()));
            }
            SaleOrderHeader order = orderOpt.get();
            Optional<CustomerAddress> customerAddressOpt = customerAddressRepository.findById(order.getCustomerAddressId());
            if (!customerAddressOpt.isPresent()) {
                throw new RuntimeException(String.format("Customer address id %s not found", order.getCustomerAddressId()));
            }
            cluster.add(customerAddressOpt.get());
        }

        // tính toán và lưu khoảng cách từ các assigned_order_item tới cluster
        // key = customer address id; value = distance from this address to cluster
        Map<UUID, BigDecimal> distanceMap = new HashMap<>();
        for (CustomerAddress from : selectedAddresses) {
            BigDecimal distance = customerAddressService.getDistanceToCluster(from, cluster);
            UUID key = from.getCustomerAddressId();
            if (!distanceMap.containsKey(key)) {
                distanceMap.put(key, distance);
            }
        }

        // sắp xếp assigned_order_item theo khoảng cách tới cluster
        List<AssignedOrderItem> sortedByCluster = new ArrayList<>();
        LinkedHashMap<UUID, BigDecimal> sortedDistanceMap = distanceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(/* Optional: Comparator.reverseOrder() */))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<UUID, BigDecimal> entry : sortedDistanceMap.entrySet()) {
            UUID key = entry.getKey();
            List<Integer> indexes = new ArrayList<>();
            for (int i = 0 ; i < orders.size() ; i++) {
                if (key.compareTo(orders.get(i).getCustomerAddressId()) == 0) {
                    // loại những item có customer address nằm trong selected customer address id ở request
                    indexes.add(i);
                }
            }
            for (Integer index : indexes) {
                AssignedOrderItem item = assignedOrderItems.get(index);
                if (!request.getAssignedOrderItemIds().contains(item.getAssignedOrderItemId().toString())) {
                    sortedByCluster.add(assignedOrderItems.get(index));
                }
            }
            log.info(String.format("Distance from customerAddressId = %s to cluster is %.10f", key, entry.getValue()));
        }

        // build response
        List<AssignedOrderItemDTO> response = new ArrayList<>();
        for (AssignedOrderItem item : sortedByCluster) {
            response.add(assignedOrderItemService.buildAssignedOrderItemDTO(item));
        }
        return response;
    }

    public DeliveryTripItem getByIdOrThrow(String itemId) {
        Optional<DeliveryTripItem> itemOpt = deliveryTripItemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            return itemOpt.get();
        }
        throw new RuntimeException(String.format("Delivery trip item with id %s is not exist", itemId));
    }
}
