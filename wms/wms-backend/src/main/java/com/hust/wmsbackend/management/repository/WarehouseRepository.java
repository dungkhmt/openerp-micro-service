package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    @Query("select w from Warehouse w join ProductWarehouse pw on pw.warehouseId = w.warehouseId " +
            "where pw.quantityOnHand > 0 and pw.productId = ?1 ")
    List<Warehouse> getWarehousesByProductId(UUID productId);
}
