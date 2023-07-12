package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.Product;
import com.hust.wmsbackend.management.model.response.ProductDetailQuantityResponse;
import com.hust.wmsbackend.management.model.response.ProductReportResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductV2Repository extends JpaRepository<Product, UUID> {

    @Query("select new com.hust.wmsbackend.management.model.response.ProductDetailQuantityResponse " +
           "(w.warehouseId, w.name, b.bayId, b.code, ii.quantityOnHandTotal, ii.importPrice, ii.lotId) " +
           "from InventoryItem ii " +
           "join Bay b on b.bayId = ii.bayId " +
           "join Warehouse w on w.warehouseId = b.warehouseId " +
           "where ii.productId = :productId and ii.isInitQuantity = true ")
    List<ProductDetailQuantityResponse> getProductDetailQuantityResponseByProductId(UUID productId);

    @Query("select new com.hust.wmsbackend.management.model.response.ProductReportResponse" +
            "(wp.productId , wp.name , " +
            "sum(wii.importPrice * wii.quantityOnHandTotal) , " +
            "sum(wii.quantityOnHandTotal) ) " +
            "from Product wp " +
            "join InventoryItem wii on wp.productId = wii.productId and wii.quantityOnHandTotal > 0 " +
            "group by wp.productId ")
    List<ProductReportResponse> getProductsDataForReport();

    @Query(value = "( select cast(wri.product_id as varchar) as productId , wp.\"name\" as productName , wri.quantity as quantity , " +
            "to_char(wri.created_stamp, 'HH24:mm:SS dd-MM-yyyy')  as effectiveDateStr , 'IMPORT' as type " +
            "from wms_receipt_item wri  " +
            "join wms_product wp on wri.product_id = wp.product_id ) " +
            "union all " +
            "( select cast(wp.product_id as varchar) as productId , wp.\"name\" as productName , wiid.quantity_on_hand_diff as quantity , " +
            "to_char(wiid.effective_date, 'HH24:mm:SS dd-MM-yyyy')  as effectiveDateStr , 'EXPORT' as type " +
            "from wms_inventory_item_detail wiid  " +
            "join wms_inventory_item wii on wiid.inventory_item_id = wii.inventory_item_id " +
            "join wms_product wp on wp.product_id = wii.product_id ) " +
            "union all " +
            "( select cast(wp.product_id as varchar) as productId , wp.\"name\" as productName , waoi.quantity as quantity , " +
            "to_char(waoi.created_stamp, 'HH24:mm:SS dd-MM-yyyy')  as effectiveDateStr , 'ASSIGNED' as type " +
            "from wms_assigned_order_item waoi  " +
            "join wms_product wp on wp.product_id = waoi.product_id " +
            "where waoi.quantity > 0 ) " +
            "union all " +
            "( select cast(wp.product_id as varchar) as productId , wp.\"name\" as productName , wdti.quantity as quantity , " +
            "to_char(wdti.created_stamp, 'HH24:mm:SS dd-MM-yyyy')  as effectiveDateStr ,  " +
            "case when wdti.status = 'CREATED' then 'WAIT_DELIVERY' " +
            "else 'DELIVERING' end as type " +
            "from wms_delivery_trip_item wdti  " +
            "join wms_assigned_order_item waoi on waoi.assigned_order_item_id = wdti.assigned_order_item_id " +
            "join wms_product wp on wp.product_id = waoi.product_id " +
            "where not wdti.is_deleted and wdti.status = 'CREATED' or wdti.status = 'DELIVERING' ) ", nativeQuery = true)
    List<ProductDiffHistoryInterface> getProductsDiffHistoryData();

    public static interface ProductDiffHistoryInterface {
        String getProductId();
        String getProductName();
        BigDecimal getQuantity();
        String getEffectiveDateStr();
        String getType();
    }
}
