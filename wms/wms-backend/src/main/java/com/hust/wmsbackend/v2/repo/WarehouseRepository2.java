package com.hust.wmsbackend.v2.repo;

import com.hust.wmsbackend.management.entity.Warehouse;
import com.hust.wmsbackend.v2.model.response.WarehouseDetailWithProduct2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository2 extends JpaRepository<Warehouse, UUID> {
    @Query("select w from Warehouse w join ProductWarehouse pw on pw.warehouseId = w.warehouseId " +
            "where pw.quantityOnHand > 0 and pw.productId = ?1 ")
    List<Warehouse> getWarehousesByProductId(UUID productId);

    @Query("SELECT new com.hust.wmsbackend.v2.model.response.WarehouseDetailWithProduct2$BayWithProduct(b.bayId, b.code, ii.productId, ii.quantityOnHandTotal) " +
            "FROM Warehouse w " +
            "JOIN Bay b ON w.warehouseId = b.warehouseId " +
            "JOIN InventoryItem ii ON b.bayId = ii.bayId " +
            "WHERE w.warehouseId = :warehouseId " +
            "AND ii.productId IN :productIds")
    public List<WarehouseDetailWithProduct2.BayWithProduct> findBayWithProductByWarehouseIdAndProductIds(
            @Param("warehouseId") UUID warehouseId,
            @Param("productIds") List<UUID> productIds
    );
}
