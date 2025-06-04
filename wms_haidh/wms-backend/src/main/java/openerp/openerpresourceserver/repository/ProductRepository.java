package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Product;
import openerp.openerpresourceserver.projection.ProductDetailProjection;
import openerp.openerpresourceserver.projection.ProductGeneralProjection;
import openerp.openerpresourceserver.projection.ProductInventoryProjection;
import openerp.openerpresourceserver.projection.ProductNameProjection;
import openerp.openerpresourceserver.projection.ProductPriceProjection;
import openerp.openerpresourceserver.projection.ProductProjection;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	@Query(value = "SELECT p.productId as id, p.code as code, p.name as name, "
			+ "COALESCE(pw.quantityOnHand, 0) as totalQuantityOnHand, " + "p.dateUpdated as dateUpdated "
			+ "FROM Product p "
			+ "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId AND pw.warehouseId = :warehouseId "
			+ "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " + "ORDER BY p.dateUpdated DESC")
	Page<ProductInventoryProjection> findProductInventory(@Param("searchTerm") String searchTerm, Pageable pageable,
			@Param("warehouseId") UUID warehouseId);
	
	@Query(value = "SELECT p.productId as id, p.code as code, p.name as name, "
	        + "COALESCE(pw.quantityOnHand, 0) as totalQuantityOnHand, p.dateUpdated as dateUpdated "
	        + "FROM Product p "
	        + "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId AND pw.warehouseId = :warehouseId "
	        + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
	        + "AND COALESCE(pw.quantityOnHand, 0) = 0 "
	        + "ORDER BY p.dateUpdated DESC")
	Page<ProductInventoryProjection> findOutOfStockProductInventory(@Param("searchTerm") String searchTerm,
	        Pageable pageable, @Param("warehouseId") UUID warehouseId);


	@Query(value = "SELECT p.productId as id, p.code as code, p.name as name, p.dateUpdated as dateUpdated "
			+ "FROM Product p " + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "ORDER BY p.dateUpdated DESC", countQuery = "SELECT COUNT(DISTINCT p.productId) " + "FROM Product p "
					+ "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<ProductGeneralProjection> findProductGeneral(String searchTerm, Pageable pageable);

	@Query("SELECT p.productId AS productId, p.name AS name, p.uom AS uom FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	List<ProductNameProjection> findProductNamesByName(@Param("searchTerm") String searchTerm);

	Optional<Product> findByCode(String code);

	@Query("""
			SELECT
			    p.productId AS productId,
			    p.name AS name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId) AS imageUrl,
			    pr.price AS price,
			    SUM(w.quantityOnHand) AS quantity,
			    p.uom AS uom,
			    p.weight AS weight
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			  AND p.categoryId = :categoryId
			GROUP BY p.productId, p.name, p.imageId, pr.price
			""")
	Page<ProductProjection> findAllProductsByCategory(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm, @Param("categoryId") UUID categoryId);

	@Query("""
			SELECT
			    p.productId AS productId,
			    p.name AS name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId) AS imageUrl,
			    pr.price AS price,
			    SUM(w.quantityOnHand) AS quantity,
			    p.uom AS uom,
			    p.weight AS weight
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			GROUP BY p.productId, p.name, p.imageId, pr.price
			""")
	Page<ProductProjection> findAllProductsWithoutCategory(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm);

	@Query("SELECT p.productId AS productId, p.code AS code, p.name AS name, p.description AS description, "
			+ "p.height AS height, p.weight AS weight, p.uom AS uom, "
			+ "CONCAT(:baseUrl, '/api/images/', p.imageId) AS imageUrl, "
			+ "(SELECT pr.price FROM ProductPrice pr WHERE pr.productId = p.productId "
			+ "AND pr.startDate <= CURRENT_DATE AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)) AS price, "
			+ "SUM(w.quantityOnHand) AS quantity " + "FROM Product p "
			+ "JOIN ProductWarehouse w ON p.productId = w.productId " + "WHERE p.productId = :productId "
			+ "GROUP BY p.productId, p.code, p.name, p.description, p.height, p.weight, p.uom, p.imageId")
	ProductDetailProjection findProductDetailById(@Param("productId") UUID productId, @Param("baseUrl") String baseUrl);

	@Query("""
			    SELECT p.productId AS productId, p.name AS name,
			           COALESCE(pp.price, 0) AS price
			    FROM Product p
			    LEFT JOIN ProductPrice pp ON p.productId = pp.productId
			    AND pp.startDate <= CURRENT_DATE
			      AND (pp.endDate IS NULL OR pp.endDate >= CURRENT_DATE)
			    WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
			""")
	Page<ProductPriceProjection> findAllWithPrice(Pageable pageable, @Param("search") String search);

}
