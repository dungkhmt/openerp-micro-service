package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	 @Query("SELECT p.productId as id, p.code as code, p.name as name, " +
	           "SUM(pw.quantityOnHand) as totalQuantityOnHand " +
	           "FROM Product p " +
	           "JOIN ProductWarehouse pw ON p.productId = pw.productId " +
	           "GROUP BY p.productId, p.code, p.name")
	    List<ProductInfoProjection> findProductInfoWithTotalQuantity();

}
