package com.hust.wmsbackend.v2.service;

import com.hust.wmsbackend.management.entity.Warehouse;
import com.hust.wmsbackend.management.model.WarehouseWithBays;
import com.hust.wmsbackend.management.model.response.ProductWarehouseResponse;
import com.hust.wmsbackend.management.model.response.WarehouseDetailsResponse;
import com.hust.wmsbackend.management.model.response.WarehouseGeneral;
import com.hust.wmsbackend.v2.model.response.WarehouseDetailWithProduct2;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    Warehouse createWarehouse(WarehouseWithBays request);

    List<WarehouseGeneral> getAllWarehouseGeneral();

    boolean delete(List<String> facilityIds);

    WarehouseWithBays getById(String id);

    List<WarehouseWithBays> getAllWarehouseDetail();

    ProductWarehouseResponse getProductInWarehouse(String warehouseId);

    Map<UUID, String> getWarehouseNameMap();

    List<WarehouseDetailsResponse> getAllWarehouseDetailWithProducts(String orderId);

    List<Warehouse> getAllWarehousesHaveProductIds(List<UUID> productIds);

    List<WarehouseDetailWithProduct2> getAllWarehouseDetailHavingProducts(List<String> productIds);
}
