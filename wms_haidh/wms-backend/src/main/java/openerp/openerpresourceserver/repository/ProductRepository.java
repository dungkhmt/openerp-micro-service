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

import openerp.openerpresourceserver.dto.response.ProductDetailResponse;
import openerp.openerpresourceserver.dto.response.ProductGeneralResponse;
import openerp.openerpresourceserver.dto.response.ProductInventoryResponse;
import openerp.openerpresourceserver.dto.response.ProductNameResponse;
import openerp.openerpresourceserver.dto.response.ProductPriceResponse;
import openerp.openerpresourceserver.dto.response.ProductResponse;
import openerp.openerpresourceserver.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
	@Query(value = "SELECT new openerp.openerpresourceserver.dto.response.ProductInventoryResponse( p.productId, p.code , p.name , "
			+ "COALESCE(pw.quantityOnHand, 0), " + "p.dateUpdated) " + "FROM Product p "
			+ "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId AND pw.warehouseId = :warehouseId "
			+ "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " + "ORDER BY p.dateUpdated DESC")
	Page<ProductInventoryResponse> findProductInventory(@Param("searchTerm") String searchTerm, Pageable pageable,
			@Param("warehouseId") UUID warehouseId);

	@Query(value = "SELECT new openerp.openerpresourceserver.dto.response.ProductInventoryResponse( p.productId, p.code, p.name, "
			+ "COALESCE(pw.quantityOnHand, 0), p.dateUpdated) " + "FROM Product p "
			+ "LEFT JOIN ProductWarehouse pw ON p.productId = pw.productId AND pw.warehouseId = :warehouseId "
			+ "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "AND COALESCE(pw.quantityOnHand, 0) = 0 " + "ORDER BY p.dateUpdated DESC")
	Page<ProductInventoryResponse> findOutOfStockProductInventory(@Param("searchTerm") String searchTerm,
			Pageable pageable, @Param("warehouseId") UUID warehouseId);

	@Query(value = "SELECT new openerp.openerpresourceserver.dto.response.ProductGeneralResponse(p.productId , p.code , p.name , p.dateUpdated) "
			+ "FROM Product p " + "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "ORDER BY p.dateUpdated DESC", countQuery = "SELECT COUNT(DISTINCT p.productId) " + "FROM Product p "
					+ "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<ProductGeneralResponse> findProductGeneral(String searchTerm, Pageable pageable);

	@Query("SELECT new openerp.openerpresourceserver.dto.response.ProductNameResponse( p.productId, p.name , p.uom) FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	List<ProductNameResponse> findProductNamesByName(@Param("searchTerm") String searchTerm);

	Optional<Product> findByCode(String code);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.ProductResponse(
			    p.productId,
			    p.name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId),
			    pr.price,
			    SUM(w.quantityOnHand),
			    p.uom ,
			    p.weight)
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			  AND p.categoryId = :categoryId
			GROUP BY p.productId, p.name, p.imageId, pr.price, p.uom, p.weight
			ORDER BY pr.price ASC
			""")
	Page<ProductResponse> findAllProductsByCategoryAsc(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm, @Param("categoryId") UUID categoryId);
	
	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.ProductResponse(
			    p.productId,
			    p.name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId),
			    pr.price,
			    SUM(w.quantityOnHand),
			    p.uom ,
			    p.weight)
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			  AND p.categoryId = :categoryId
			GROUP BY p.productId, p.name, p.imageId, pr.price, p.uom, p.weight
			ORDER BY pr.price DESC
			""")
	Page<ProductResponse> findAllProductsByCategoryDesc(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm, @Param("categoryId") UUID categoryId);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.ProductResponse(
			    p.productId,
			    p.name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId),
			    pr.price,
			    SUM(w.quantityOnHand) ,
			    p.uom ,
			    p.weight)
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			GROUP BY p.productId, p.name, p.imageId, pr.price, p.uom, p.weight
	        ORDER BY pr.price ASC
			""")
	Page<ProductResponse> findAllProductsWithoutCategoryAsc(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm);
	
	@Query("""
			SELECT new openerp.openerpresourceserver.dto.response.ProductResponse(
			    p.productId,
			    p.name,
			    CONCAT(:baseUrl, '/api/images/', p.imageId),
			    pr.price,
			    SUM(w.quantityOnHand) ,
			    p.uom ,
			    p.weight)
			FROM Product p
			JOIN ProductPrice pr ON p.productId = pr.productId
			    AND pr.startDate <= CURRENT_DATE
			    AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			JOIN ProductWarehouse w ON p.productId = w.productId
			WHERE (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
			GROUP BY p.productId, p.name, p.imageId, pr.price, p.uom, p.weight
	        ORDER BY pr.price DESC
			""")
	Page<ProductResponse> findAllProductsWithoutCategoryDesc(Pageable pageable, @Param("baseUrl") String baseUrl,
			@Param("searchTerm") String searchTerm);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.ProductDetailResponse(
			        p.productId,
			        p.code,
			        p.name,
			        p.description,
			        p.height,
			        p.weight,
			        p.uom,
			        CONCAT(:baseUrl, '/api/images/', p.imageId),
			        (
			            SELECT pr.price
			            FROM ProductPrice pr
			            WHERE pr.productId = p.productId
			              AND pr.startDate <= CURRENT_DATE
			              AND (pr.endDate IS NULL OR pr.endDate >= CURRENT_DATE)
			            ORDER BY pr.startDate DESC
			        ),
			        SUM(w.quantityOnHand)
			    )
			    FROM Product p
			    JOIN ProductWarehouse w ON p.productId = w.productId
			    WHERE p.productId = :productId
			    GROUP BY p.productId, p.code, p.name, p.description, p.height, p.weight, p.uom, p.imageId
			""")
	ProductDetailResponse findProductDetailById(@Param("productId") UUID productId, @Param("baseUrl") String baseUrl);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.ProductPriceResponse( p.productId, p.name,
			           COALESCE(pp.price, 0))
			    FROM Product p
			    LEFT JOIN ProductPrice pp ON p.productId = pp.productId
			    AND pp.startDate <= CURRENT_DATE
			      AND (pp.endDate IS NULL OR pp.endDate >= CURRENT_DATE)
			    WHERE (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))
			""")
	Page<ProductPriceResponse> findAllWithPrice(Pageable pageable, @Param("search") String search);

}
