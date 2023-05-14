package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.Product;
import com.hust.wmsbackend.management.model.response.ProductDetailQuantityResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

}
