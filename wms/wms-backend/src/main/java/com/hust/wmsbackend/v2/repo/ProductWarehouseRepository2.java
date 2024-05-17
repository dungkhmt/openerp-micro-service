package com.hust.wmsbackend.v2.repo;

import com.hust.wmsbackend.management.entity.ProductWarehouse;
import com.hust.wmsbackend.management.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductWarehouseRepository2 extends JpaRepository<ProductWarehouse, UUID> {

    Optional<ProductWarehouse> findProductWarehouseByWarehouseId(UUID warehouseId);

    Optional<ProductWarehouse> findProductWarehouseByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

    List<ProductWarehouse> findAllByProductId(UUID productId);

    @Query("select sum(pw.quantityOnHand) from ProductWarehouse pw where pw.productId = :productId")
    BigDecimal getTotalOnHandQuantityByProductId(UUID productId);

    List<ProductWarehouse> findAllByWarehouseId(UUID warehouseId);

    @Query("select wh from Warehouse wh join ProductWarehouse pw on pw.warehouseId = wh.warehouseId " +
            "where pw.quantityOnHand > 0 and pw.productId = :productId")
    List<Warehouse> findAllWarehouseByProductId(UUID productId);
}
