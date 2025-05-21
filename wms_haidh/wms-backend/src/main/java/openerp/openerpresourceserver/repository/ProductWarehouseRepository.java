package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.ProductWarehouse;

@Repository
public interface ProductWarehouseRepository extends JpaRepository<ProductWarehouse, UUID> {

	@Query("SELECT COALESCE(SUM(pw.quantityOnHand), 0) FROM ProductWarehouse pw WHERE pw.productId = :productId")
    double getTotalQuantityByProductId(@Param("productId") UUID productId);
}

