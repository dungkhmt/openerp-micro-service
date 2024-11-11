package openerp.openerpresourceserver.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ProductWarehouse;

@Repository
public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, UUID> {

//    Optional<ProductWarehouse> findProductWarehouseByWarehouseId(UUID warehouseId);
//
//    Optional<ProductWarehouse> findProductWarehouseByWarehouseIdAndProductId(UUID warehouseId, UUID productId);
//
//    List<ProductWarehouse> findAllByProductId(UUID productId);

    @Query("select sum(pw.quantityOnHand) from ProductWarehouse pw where pw.productId = :productId")
    BigDecimal getTotalOnHandQuantityByProductId(UUID productId);

//    List<ProductWarehouse> findAllByWarehouseId(UUID warehouseId);
}

