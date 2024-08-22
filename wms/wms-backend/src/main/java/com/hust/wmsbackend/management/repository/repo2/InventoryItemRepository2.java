package com.hust.wmsbackend.management.repository.repo2;

import com.hust.wmsbackend.management.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryItemRepository2 extends JpaRepository<InventoryItem, UUID> {

    @Query("select ii from InventoryItem ii " +
            "where ii.quantityOnHandTotal > 0 and ii.productId = ?1 and ii.bayId = ?2 and ii.warehouseId = ?3 "
            + "order by ii.datetimeReceived ASC" )
    List<InventoryItem> getInventoryItemByProductIdAndBayIdAndWarehouseIdOrderByDatetimeReceivedHavingQuantity(UUID productId, UUID bayId, UUID warehouseId);

    List<InventoryItem> findAllByWarehouseId(UUID warehouseId);

    @Query("select ii from InventoryItem ii join Warehouse w on w.warehouseId = ?1 and w.warehouseId = ii.warehouseId " +
            "join Product p on p.productId = ?2 and p.productId = ii.productId " +
            "where ii.quantityOnHandTotal > 0 ")
    List<InventoryItem> findAllByWarehouseIdAndHavingProductId(UUID warehouseId, UUID productId);

    List<InventoryItem> findAllByProductId(UUID productId);

    List<InventoryItem> findAllByBayId(UUID bayId);

}
