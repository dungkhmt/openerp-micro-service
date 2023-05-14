package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.ProductWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, UUID> {

    Optional<ProductWarehouse> findProductWarehouseByWarehouseId(UUID warehouseId);

    Optional<ProductWarehouse> findProductWarehouseByWarehouseIdAndProductId(UUID warehouseId, UUID productId);

    @Query("select sum(pw.quantityOnHand) from ProductWarehouse pw where pw.productId = :productId")
    BigDecimal getTotalOnHandQuantityByProductId(UUID productId);

}
