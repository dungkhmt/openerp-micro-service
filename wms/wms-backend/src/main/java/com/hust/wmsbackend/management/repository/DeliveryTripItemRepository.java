package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.DeliveryTripItem;
import com.hust.wmsbackend.management.model.response.ProductCategoryMonthlyData;
import com.hust.wmsbackend.management.model.response.ReportDataPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryTripItemRepository extends JpaRepository<DeliveryTripItem, String> {

    List<DeliveryTripItem> findAllByDeliveryTripIdAndIsDeletedIsFalse(String deliveryTripId);

    List<DeliveryTripItem> findAllByDeliveryTripIdAndIsDeleted(String deliveryTripId, boolean isDeleted);

    Optional<DeliveryTripItem> findByDeliveryTripItemIdAndIsDeletedIsFalse(String id);

    @Query(value = "select sum(dti.quantity) from wms_delivery_trip_item dti " +
           "join wms_assigned_order_item aoi on aoi.assigned_order_item_id = dti.assigned_order_item_id " +
           "join wms_product p on p.product_id = aoi.product_id " +
           "where dti.order_id = ?1 and dti.is_deleted = false and (dti.status = 'DONE' or dti.status = 'FAIL') " +
           "and p.product_id = ?2 ", nativeQuery = true)
    Long getTotalCompleteDeliveryItemByOrderIdAndProductId(UUID orderId, UUID productId);

    @Query(value = "select sum(dti.quantity) from wms_delivery_trip_item dti " +
                   "join wms_assigned_order_item aoi on aoi.assigned_order_item_id = dti.assigned_order_item_id " +
                   "join wms_product p on p.product_id = aoi.product_id " +
                   "where dti.order_id = ?1 and dti.is_deleted = false and dti.status = 'DONE' " +
                   "and p.product_id = ?2 ", nativeQuery = true)
    Long getTotalDoneDeliveryItemByOrderIdAndProductId(UUID orderId, UUID productId);

    @Query(value = "select sum(dti.quantity) from wms_delivery_trip_item dti " +
            "join wms_assigned_order_item aoi on aoi.assigned_order_item_id = dti.assigned_order_item_id " +
            "join wms_product p on p.product_id = aoi.product_id " +
            "where dti.order_id = ?1 and dti.is_deleted = false and dti.status = 'FAIL' " +
            "and p.product_id = ?2 ", nativeQuery = true)
    Long getTotalFailDeliveryItemByOrderIdAndProductId(UUID orderId, UUID productId);

    @Query("select dti from DeliveryTripItem dti join SaleOrderHeader soh on soh.orderId = dti.orderId where soh.userLoginId = ?1 and dti.isDeleted = false ")
    List<DeliveryTripItem> getDeliveryTripItemByUserLoginId(String userLoginId);

    @Query("select new com.hust.wmsbackend.management.model.response.ReportDataPoint " +
            "(to_char(date_trunc('month', ws.expectedDeliveryStamp), 'yyyy- MM- dd') , sum(wsoi.priceUnit)) " +
            "from DeliveryTripItem wdti " +
            "join DeliveryTrip wdt on wdti.deliveryTripId = wdt.deliveryTripId and wdt.isDeleted = false " +
            "join Shipment ws on wdt.shipmentId = ws.shipmentId and ws.isDeleted = false " +
            "join SaleOrderHeader wsoh on wdti.orderId = wsoh.orderId " +
            "join SaleOrderItem wsoi on wsoi.orderId = wsoh.orderId " +
            "join AssignedOrderItem waoi on wdti.assignedOrderItemId = waoi.assignedOrderItemId and waoi.productId = wsoi.productId " +
            "where wdti.status = 'DONE' " +
            "group by date_trunc('month', ws.expectedDeliveryStamp) ")
    List<ReportDataPoint> getDataPointsForRevenue();

    @Query("select new com.hust.wmsbackend.management.model.response.ReportDataPoint" +
            "(to_char(date_trunc('month', ws.expectedDeliveryStamp), 'yyyy- MM- dd') , sum(wsoi.priceUnit - wii.importPrice)) " +
            "from DeliveryTripItem wdti " +
            "join DeliveryTrip wdt on wdti.deliveryTripId = wdt.deliveryTripId and wdt.isDeleted = false " +
            "join Shipment ws on wdt.shipmentId = ws.shipmentId and ws.isDeleted = false " +
            "join AssignedOrderItem waoi on waoi.assignedOrderItemId = wdti.assignedOrderItemId " +
            "join SaleOrderHeader wsoh on wdti.orderId = wsoh.orderId " +
            "join SaleOrderItem wsoi on wsoi.orderId = wsoh.orderId and waoi.productId = wsoi.productId " +
            "join InventoryItem wii on waoi.inventoryItemId = wii.inventoryItemId " +
            "where wdti.status = 'DONE' " +
            "group by date_trunc('month', ws.expectedDeliveryStamp) ")
    List<ReportDataPoint> getDataPointsForProfit();

    @Query("select new com.hust.wmsbackend.management.model.response.ProductCategoryMonthlyData " +
            "(to_char(date_trunc('month', ws.expectedDeliveryStamp), 'MM-yyyy' ), wpc.name , sum(wsoi.priceUnit - wii.importPrice)) " +
            "from DeliveryTripItem wdti " +
            "join AssignedOrderItem waoi on wdti.assignedOrderItemId = waoi.assignedOrderItemId " +
            "join InventoryItem wii on waoi.inventoryItemId = wii.inventoryItemId " +
            "join SaleOrderHeader wsoh on wdti.orderId = wsoh.orderId " +
            "join SaleOrderItem wsoi on wsoi.orderId = wdti.orderId and wsoi.productId = waoi.productId " +
            "join DeliveryTrip wdt on wdt.deliveryTripId = wdti.deliveryTripId and wdt.isDeleted = false " +
            "join Shipment ws on ws.shipmentId = wdt.shipmentId and ws.isDeleted = false " +
            "join Product wp on wp.productId = waoi.productId " +
            "join ProductCategory wpc on wpc.categoryId = wp.categoryId " +
            "where wdti.status = 'DONE' " +
            "group by date_trunc('month', ws.expectedDeliveryStamp) , wpc.name ")
    List<ProductCategoryMonthlyData> getProductCategoryMonthlyData();
}
