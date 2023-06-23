package com.hust.wmsbackend.management.service.impl;

import com.hust.wmsbackend.management.cache.RedisCacheService;
import com.hust.wmsbackend.management.entity.*;
import com.hust.wmsbackend.management.model.WarehouseWithBays;
import com.hust.wmsbackend.management.model.response.ProductWarehouseResponse;
import com.hust.wmsbackend.management.model.response.WarehouseDetailsResponse;
import com.hust.wmsbackend.management.repository.*;
import com.hust.wmsbackend.management.service.WarehouseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private WarehouseRepository warehouseRepository;
    private InventoryItemRepository inventoryItemRepository;
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
        Warehouse newWarehouse = Warehouse.builder()
                                          .name(request.getName())
                                          .address(request.getAddress())
                                          .width(request.getWarehouseWidth())
                                          .length(request.getWarehouseLength())
                                          .code(request.getCode())
                                          .latitude(request.getLatitude())
                                          .longitude(request.getLongitude())
                                          .build();

        List<Bay> prevBays;
        if (request.getId() != null) {
            UUID warehouseIdUUID = UUID.fromString(request.getId());
            newWarehouse.setWarehouseId(warehouseIdUUID);
            prevBays = bayRepository.findAllByWarehouseId(warehouseIdUUID);
        } else {
            prevBays = new ArrayList<>();
        }

        Warehouse warehouse = warehouseRepository.save(newWarehouse);
        log.info("Start save list shelf");
        List<WarehouseWithBays.Shelf> listShelf = request.getListShelf();
        if (listShelf != null && !listShelf.isEmpty()) {
            UUID warehouseId = warehouse.getWarehouseId();
            List<Bay> bays = listShelf.stream()
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
        log.info(String.format("Saved warehouse entity with id %s", warehouse.getWarehouseId()));
        // update redis
        List<Warehouse> warehouses = redisCacheService.getCachedListObject(RedisCacheService.ALL_WAREHOUSES_KEY, Warehouse.class);
        if (warehouses != null && !warehouses.isEmpty()) {
            warehouses.add(warehouse);
        }
        redisCacheService.setCachedValueWithExpire(RedisCacheService.ALL_WAREHOUSES_KEY, warehouses);
        return warehouse;
    }

    @Override
    public List<Warehouse> getAllWarehouseGeneral() {
        log.info("Start get all warehouse in service");
        List<Warehouse> response = getWarehouseInCacheElseDatabase();
        // TODO: Filter by company or something else... user can not view all facility in database
        log.info(String.format("Get %d facilities", response.size()));
        return response;
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
                                                                                           .id(bay
                                                                                                   .getBayId()
                                                                                                   .toString())
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
