package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.cache.RedisCacheService;
import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.ReceiptRequest;
import com.hust.wmsbackend.management.model.WarehouseWithBays;
import com.hust.wmsbackend.management.model.response.ProductWarehouseResponse;
import com.hust.wmsbackend.management.model.response.WarehouseDetailsResponse;
import com.hust.wmsbackend.management.model.response.WarehouseGeneral;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private BayRepository bayRepository;
    private ProductWarehouseRepository productWarehouseRepository;
    private ReceiptRepository receiptRepository;
    private ReceiptItemRequestRepository receiptItemRequestRepository;
    private DeliveryTripRepository deliveryTripRepository;
    private WarehouseRepository warehouseRepository;
    private InventoryItemRepository inventoryItemRepository;
    private ReceiptItemRepository receiptItemRepository;
    private AssignedOrderItemRepository assignedOrderItemRepository;
    private ProductV2Repository productRepository;
    private SaleOrderItemRepository saleOrderItemRepository;

    private RedisCacheService redisCacheService;

    private List<Warehouse> getWarehouseInCacheElseDatabase() {
        List<Warehouse> warehouses = redisCacheService.getCachedListObject(RedisCacheService.ALL_WAREHOUSES_KEY, Warehouse.class);
        if (warehouses == null) {
            warehouses = warehouseRepository.findAll();
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_WAREHOUSES_KEY, warehouses);
        }
        return warehouses;
    }

    @Transactional
    @Override
    public Warehouse createWarehouse(WarehouseWithBays request) {
        log.info(String.format("Start create warehouse with request %s", request));

        // update hoặc tạo mới thông tin chung của kho
        Warehouse newWarehouse;
        List<Bay> prevBays;
        UUID warehouseId;
        if (request.getId() != null) {
            warehouseId = UUID.fromString(request.getId());
            Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
            if (!warehouseOpt.isPresent()) {
                log.error(String.format("Warehouse with id %s is not found", request.getId()));
                return null;
            }
            Warehouse warehouse = warehouseOpt.get();
            warehouse.setName(request.getName());
            warehouse.setAddress(request.getAddress());
            warehouse.setWidth(request.getWarehouseWidth());
            warehouse.setLength(request.getWarehouseLength());
            warehouse.setCode(request.getCode());
            warehouse.setLatitude(request.getLatitude());
            warehouse.setLongitude(request.getLongitude());
            warehouseRepository.save(warehouse);
            prevBays = bayRepository.findAllByWarehouseId(warehouseId);
            newWarehouse = warehouse;
        } else {
            newWarehouse = Warehouse.builder()
                    .name(request.getName())
                    .address(request.getAddress())
                    .width(request.getWarehouseWidth())
                    .length(request.getWarehouseLength())
                    .code(request.getCode())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build();
            Warehouse savedWarehouse = warehouseRepository.save(newWarehouse);
            newWarehouse = savedWarehouse;
            warehouseId = savedWarehouse.getWarehouseId();
            prevBays = new ArrayList<>();
        }

        log.info("Start save list shelf");
        List<WarehouseWithBays.Shelf> listShelf = request.getListShelf();
        if (listShelf != null && !listShelf.isEmpty()) {
            List<Bay> bays = listShelf.stream()
                    .filter(shelf -> shelf.getCode() != null
                            && shelf.getX() != null
                            && shelf.getY() != null
                            && shelf.getLength() != null
                            && shelf.getWidth() != null)
                                      .map(shelf -> {
                                          Bay bay = Bay.builder()
                                                       .warehouseId(warehouseId)
                                                       .code(shelf.getCode())
                                                       .x(shelf.getX())
                                                       .y(shelf.getY())
                                                       .xLong(shelf.getWidth())
                                                       .yLong(shelf.getLength())
                                                       .build();
                                          if (shelf.getId() != null) {
                                              bay.setBayId(UUID.fromString(shelf.getId()));
                                          }
                                          return bay;
                                      })
                                      .collect(Collectors.toList());

            List<UUID> newBayIds = bays.stream().map(Bay::getBayId).collect(Collectors.toList());
            List<Bay> deletedBays = prevBays.stream().filter(bay -> !newBayIds.contains(bay.getBayId())).collect(
                Collectors.toList());

            bayRepository.deleteAll(deletedBays);
            bayRepository.saveAll(bays);

            log.info(String.format("Saved bay list for warehouse id %s", warehouseId));
        }
        log.info(String.format("Saved warehouse entity with id %s", newWarehouse.getWarehouseId()));
        // update redis
        List<Warehouse> warehouses = redisCacheService.getCachedListObject(RedisCacheService.ALL_WAREHOUSES_KEY, Warehouse.class);
        if (warehouses != null && !warehouses.isEmpty()) {
            int index = -1;
            for (int i = 0; i < warehouses.size(); i++) {
                Warehouse warehouse = warehouses.get(i);
                if (warehouse.getWarehouseId().toString().equals(request.getId())) {
                    index = i;
                }
            }
            if (index != - 1) {
                warehouses.remove(index);
            }
            warehouses.add(newWarehouse);
        }
        redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_WAREHOUSES_KEY, warehouses);
        return newWarehouse;
    }

    @Override
    public List<WarehouseGeneral> getAllWarehouseGeneral() {
        log.info("Start get all warehouse in service");
        List<Warehouse> response = getWarehouseInCacheElseDatabase();
        return response.stream().map(warehouse -> new WarehouseGeneral(warehouse, checkBeDelete(warehouse.getWarehouseId())))
                .collect(Collectors.toList());
    }

    private boolean checkBeDelete(UUID warehouseId) {
        List<InventoryItem> inventoryItemList = inventoryItemRepository.findAllByWarehouseId(warehouseId);
        if (!inventoryItemList.isEmpty()) {
            return false;
        }
        List<ProductWarehouse> productWarehouseList = productWarehouseRepository.findAllByWarehouseId(warehouseId);
        if (!productWarehouseList.isEmpty()) {
            return false;
        }
        List<ReceiptItemRequest> receiptItemRequestList = receiptItemRequestRepository.findAllByWarehouseId(warehouseId);
        if (!receiptItemRequestList.isEmpty()) {
            return false;
        }
        List<Bay> bayList = bayRepository.findAllByWarehouseId(warehouseId);
        if (!bayList.isEmpty()) {
            return false;
        }
        List<Receipt> receiptList = receiptRepository.findAllByWarehouseId(warehouseId);
        if (!receiptList.isEmpty()) {
            return false;
        }
        List<AssignedOrderItem> assignedOrderItemList = assignedOrderItemRepository.findAllByWarehouseId(warehouseId);
        if (!assignedOrderItemList.isEmpty()) {
            return false;
        }
        List<DeliveryTrip> deliveryTripList = deliveryTripRepository.findAllByWarehouseId(warehouseId);
        if (!deliveryTripList.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean delete(List<String> warehouseIds) {
        if (warehouseIds.isEmpty()) {
            log.info("Empty warehouse id list for delete");
            return true;
        }
        try {
            for (String warehouseId : warehouseIds) {
                log.info(String.format("Start delete data about warehouse id: %s", warehouseId));
                UUID id = UUID.fromString(warehouseId);
                bayRepository.deleteBaysByWarehouseId(id);
                warehouseRepository.deleteById(id);
            }
            log.info("Deleted warehouse ids: " + warehouseIds);
            // update redis
            List<Warehouse> warehouses = redisCacheService.getCachedListObject(RedisCacheService.ALL_WAREHOUSES_KEY, Warehouse.class);
            if (warehouses != null && !warehouses.isEmpty()) {
                warehouses.removeIf(warehouse -> warehouseIds.contains(warehouse.getWarehouseId().toString()));
            }
            redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_WAREHOUSES_KEY, warehouses);
            return true;
        } catch (Exception e) {
            log.info("Error when deleting warehouse ids: " + warehouseIds);
            return false;
        }
    }

    @Override
    public WarehouseWithBays getById(String id) {
        UUID uuid = UUID.fromString(id);
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(uuid);
        if (warehouseOpt.isPresent()) {
            Warehouse warehouse = warehouseOpt.get();
            List<Bay> bays = bayRepository.findAllByWarehouseId(uuid);
            List<WarehouseWithBays.Shelf> shelves = bays.stream()
                                                        .map(bay -> WarehouseWithBays.Shelf.builder()
                                                        .code(bay.getCode())
                                                        .width(bay.getXLong())
                                                        .length(bay.getYLong())
                                                        .x(bay.getX())
                                                        .y(bay.getY())
                                                        .id(bay.getBayId().toString())
                                                        .canBeDelete(checkBayCanBeDelete(bay.getBayId()))
                                                        .build())
                                                    .collect(Collectors.toList());
            return WarehouseWithBays.builder()
                                    .id(warehouse.getWarehouseId().toString())
                                    .warehouseLength(warehouse.getLength())
                                    .warehouseWidth(warehouse.getWidth())
                                    .name(warehouse.getName())
                                    .code(warehouse.getCode())
                                    .latitude(warehouse.getLatitude())
                                    .longitude(warehouse.getLongitude())
                                    .address(warehouse.getAddress())
                                    .listShelf(shelves)
                                    .build();
        } else {
            log.warn(String.format("Not found warehouse with id %s", id));
            return null;
        }
    }

    private boolean checkBayCanBeDelete(UUID bayId) {
        List<InventoryItem> inventoryItemList = inventoryItemRepository.findAllByBayId(bayId);
        if (!inventoryItemList.isEmpty()) {
            return false;
        }
        List<ReceiptItem> receiptItemList = receiptItemRepository.findAllByBayId(bayId);
        if (!receiptItemList.isEmpty()) {
            return false;
        }
        List<AssignedOrderItem> assignedOrderItemList = assignedOrderItemRepository.findAllByBayId(bayId);
        if (!assignedOrderItemList.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public List<WarehouseWithBays> getAllWarehouseDetail() {
        List<Warehouse> warehouseGeneral = getWarehouseInCacheElseDatabase() ;
        return warehouseGeneral.stream()
                               .map(general -> getById(general.getWarehouseId().toString()))
                               .collect(Collectors.toList());
    }

    @Override
    public ProductWarehouseResponse getProductInWarehouse(String warehouseIdStr) {
        UUID warehouseId = UUID.fromString(warehouseIdStr);
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (!warehouseOpt.isPresent()) {
            log.warn(String.format("Warehouse id %s is not exist", warehouseIdStr));
            return null;
        }
        List<InventoryItem> items = inventoryItemRepository.findAllByWarehouseId(warehouseId);
        BigDecimal totalImportPrice = BigDecimal.ZERO;
        BigDecimal totalExportPrice = BigDecimal.ZERO;
        List<ProductWarehouseResponse.ProductWarehouseDetailResponse> products = new ArrayList<>();
        for (InventoryItem item : items) {
            if (item.getQuantityOnHandTotal().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            Optional<Bay> bayOpt = bayRepository.findById(item.getBayId());
            Bay bay = bayOpt.get();
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            Product product = productOpt.get();
            ProductWarehouseResponse.ProductWarehouseDetailResponse productDetail = ProductWarehouseResponse
                .ProductWarehouseDetailResponse
                .builder()
                .productId(item.getProductId().toString())
                .productName(product.getName())
                .quantity(item.getQuantityOnHandTotal())
                .lotId(item.getLotId())
                .bayId(item.getBayId().toString())
                .bayCode(bay.getCode())
                .importPrice(item.getImportPrice())
//                .exportPrice(item.getExportPrice())
                // TODO: Re-calculate export price by product_price table
                .build();
            products.add(productDetail);
            totalImportPrice = totalImportPrice.add(item.getImportPrice());
//            totalExportPrice = totalExportPrice.add(item.getExportPrice());
        }
        return ProductWarehouseResponse
            .builder()
            .warehouseId(warehouseIdStr)
            .products(products)
            .totalImportPrice(totalImportPrice)
            .totalExportPrice(totalExportPrice)
            .build();
    }

    @Override
    public Map<UUID, String> getWarehouseNameMap() {
        List<Warehouse> warehouses = getWarehouseInCacheElseDatabase();
        Map<UUID, String> map = new HashMap<>();
        for (Warehouse warehouse : warehouses) {
            map.put(warehouse.getWarehouseId(), warehouse.getName());
        }
        return map;
    }

    @Override
    public List<WarehouseDetailsResponse> getAllWarehouseDetailWithProducts(String orderId) {
        // chỉ lấy các warehouse có hàng của order
        List<SaleOrderItem> items = saleOrderItemRepository.findAllByOrderId(UUID.fromString(orderId));
        Set<UUID> productIds = items.stream().map(SaleOrderItem::getProductId).collect(Collectors.toSet());

        List<WarehouseWithBays> warehouses = getAllWarehouseHavingProducts(productIds);
        List<WarehouseDetailsResponse> response = new ArrayList<>();
        for (WarehouseWithBays warehouse : warehouses) {
            // chỉ lấy các bay có product của order cần tìm
            List<ProductWarehouseResponse.ProductWarehouseDetailResponse> products = getProductInWarehouse(warehouse.getId())
                    .getProducts()
                    .stream()
                    .filter(product -> productIds.contains(UUID.fromString(product.getProductId())))
                    .collect(Collectors.toList());
            WarehouseDetailsResponse adder = WarehouseDetailsResponse
                .builder()
                .info(warehouse)
                .items(products)
                .build();
            response.add(adder);
        }
        return response;
    }

    private List<WarehouseWithBays> getAllWarehouseHavingProducts(Set<UUID> productIds) {
        Set<UUID> warehouseIds = new HashSet<>();
        for (UUID productId : productIds) {
            List<Warehouse> warehouses = warehouseRepository.getWarehousesByProductId(productId);
            warehouseIds.addAll(warehouses.stream().map(Warehouse::getWarehouseId).collect(Collectors.toSet()));
        }
        return warehouseIds.stream()
                .map(warehouseId -> getById(warehouseId.toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Warehouse> getAllWarehousesHaveProductIds(List<UUID> productIds) {
        List<Warehouse> response = new ArrayList<>();
        List<Warehouse> warehouses = getWarehouseInCacheElseDatabase();
        for (Warehouse warehouse : warehouses) {
            List<InventoryItem> inventoryItems = inventoryItemRepository.findAllByWarehouseId(warehouse.getWarehouseId());
            for (InventoryItem item : inventoryItems) {
                boolean hasQuantity = item.getQuantityOnHandTotal().compareTo(BigDecimal.ZERO) > 0;
                if (productIds.contains(item.getProductId()) && hasQuantity) {
                    response.add(warehouse);
                    break;
                }
            }
        }
        return response;
    }

}
