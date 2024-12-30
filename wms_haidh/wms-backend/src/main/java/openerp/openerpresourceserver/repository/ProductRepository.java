package openerp.openerpresourceserver.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.entity.ProductInfoProjection;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	@Query(value = "SELECT p.productId as id, p.code as code, p.name as name, "
            + "COALESCE(SUM(pw.quantityOnHand), 0) as totalQuantityOnHand, "
            + "p.dateUpdated as dateUpdated "
            + "FROM Product p "
            + "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId "
            + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
            + "GROUP BY p.productId, p.code, p.name, p.dateUpdated "
            + "ORDER BY p.dateUpdated DESC",
            countQuery = "SELECT COUNT(DISTINCT p.productId) "
            + "FROM Product p "
            + "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId "
            + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	    Page<ProductInfoProjection> findProductInfoWithTotalQuantity(
	            @Param("searchTerm") String searchTerm, 
	            Pageable pageable);
    Optional<Product> findByCode(String code);
}







