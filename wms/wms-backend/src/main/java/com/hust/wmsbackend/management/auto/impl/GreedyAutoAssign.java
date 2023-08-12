package com.hust.wmsbackend.management.auto.impl;

import com.hust.wmsbackend.management.auto.AutoAssignOrderItem;
import com.hust.wmsbackend.management.auto.DistanceCalculator;
import com.hust.wmsbackend.management.auto.OrderItem;
import com.hust.wmsbackend.management.entity.InventoryItem;
import com.hust.wmsbackend.management.entity.Warehouse;
import com.hust.wmsbackend.management.model.response.AutoAssignOrderItemResponse;
import com.hust.wmsbackend.management.model.response.OrderDetailResponse;
import com.hust.wmsbackend.management.repository.InventoryItemRepository;
import com.hust.wmsbackend.management.service.BayService;
import com.hust.wmsbackend.management.service.OrderService;
import com.hust.wmsbackend.management.service.ProductService;
import com.hust.wmsbackend.management.service.WarehouseService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GreedyAutoAssign implements AutoAssignOrderItem {

    private DistanceCalculator distanceCalculator;

    private InventoryItemRepository inventoryItemRepository;

    private WarehouseService warehouseService;
    private OrderService orderService;
    private BayService bayService;
    private ProductService productService;

    @Override
    public AutoAssignOrderItemResponse assign(String orderId) {
        Map<UUID, String> productNameMap = productService.getProductNameMap();
        Map<UUID, String> bayCodeMap = bayService.getBayCodeMap();
        Map<UUID, String> warehouseNameMap = warehouseService.getWarehouseNameMap();
        OrderDetailResponse orderInfo = orderService.getOrderDetailById(orderId);
        List<OrderItem> items = orderInfo
            .getRemainingItems()
            .stream()
            .map(item -> OrderItem.builder().productId(item.getProductId()).quantity(item.getQuantity()).build())
            .collect(Collectors.toList());
        double cusAddLon = orderInfo.getCusAddLon().doubleValue();
        double cusAddLat = orderInfo.getCusAddLat().doubleValue();

        Map<UUID, BigDecimal> itemsMap = new HashMap<>();
        for (OrderItem item : items) {
            itemsMap.put(item.getProductId(), item.getQuantity());
        }
        List<UUID> productIds = items.stream().map(OrderItem::getProductId).collect(Collectors.toList());
        List<Warehouse> warehouses = warehouseService.getAllWarehousesHaveProductIds(productIds);
        Map<UUID, Double> distMap;
        try {
            distMap = distanceCalculator.getWarehouseCusAddMap(cusAddLon, cusAddLat, warehouses);
        } catch (RuntimeException e) {
            return null;
        }
        Map<UUID, Double> sortedDistMap = distMap.entrySet()
            .stream().sorted(Map.Entry.comparingByValue())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        List<OrderDetailResponse.ProcessedOrderItemResponse> processingItems = new ArrayList<>();
        for (Map.Entry<UUID, Double> warehouseEntry : sortedDistMap.entrySet()) {
            System.out.printf("Warehouse entry key %s, distance %f%n", warehouseEntry.getKey(), warehouseEntry.getValue());
            List<InventoryItem> inventoryItems = inventoryItemRepository.findAllByWarehouseId(warehouseEntry.getKey())
                .stream().filter(item -> itemsMap.containsKey(item.getProductId())).collect(Collectors.toList());
            for (InventoryItem inventoryItem : inventoryItems) {
                UUID productId = inventoryItem.getProductId();
                if (itemsMap.get(productId).compareTo(BigDecimal.ZERO) <= 0 ||
                    inventoryItem.getQuantityOnHandTotal().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                BigDecimal needQuantity = itemsMap.get(productId);
                BigDecimal existQuantity = inventoryItem.getQuantityOnHandTotal();
                boolean warehouseHasEnoughQuantity = existQuantity.compareTo(needQuantity) > 0;
                OrderDetailResponse.ProcessedOrderItemResponse adder = OrderDetailResponse.ProcessedOrderItemResponse
                    .builder()
                    .productId(productId)
                    .bayId(inventoryItem.getBayId())
                    .warehouseId(inventoryItem.getWarehouseId())
                    .lotId(inventoryItem.getLotId())
                    .warehouseName(warehouseNameMap.get(inventoryItem.getWarehouseId()))
                    .productName(productNameMap.get(productId))
                    .bayCode(bayCodeMap.get(inventoryItem.getBayId()))
                    .build();
                if (warehouseHasEnoughQuantity) {
                    adder.setQuantity(needQuantity);
                    itemsMap.put(productId, BigDecimal.ZERO);
                } else {
                    BigDecimal newItemMapQuantity = needQuantity.subtract(existQuantity);
                    itemsMap.put(productId, newItemMapQuantity);
                    adder.setQuantity(existQuantity);
                    inventoryItem.setQuantityOnHandTotal(BigDecimal.ZERO); // need?
                }
                processingItems.add(adder);
            }
        }

        List<OrderDetailResponse.OrderItemResponse> remainingItems = new ArrayList<>();
        for (Map.Entry<UUID, BigDecimal> entry : itemsMap.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                UUID localProductId = entry.getKey();
                BigDecimal remainQuantity = entry.getValue();
                remainingItems.add(OrderDetailResponse.OrderItemResponse.builder()
                    .productId(localProductId)
                    .productName(productNameMap.get(localProductId))
                    .quantity(remainQuantity)
                    .build());
            }
        }
        return AutoAssignOrderItemResponse.builder()
            .processingItems(processingItems)
            .remainingItems(remainingItems)
            .build();
    }
}
