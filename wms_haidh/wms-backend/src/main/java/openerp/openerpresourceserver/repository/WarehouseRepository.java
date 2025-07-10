package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

	List<Warehouse> findAll();

	@Query("SELECT w FROM Warehouse w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))")
	Page<Warehouse> searchByName(@Param("search") String search, Pageable pageable);

	@Query("""
			    SELECT DISTINCT w
			    FROM Warehouse w
			    JOIN AssignedOrderItem aoi ON aoi.warehouseId = w.warehouseId
			    JOIN Order o ON o.orderId = aoi.orderId
			    WHERE aoi.status='PICKED' AND (o.status = 'PICK_COMPLETE' OR o.status='DELIVERING')
			""")
	List<Warehouse> findWarehousesWithPickedOrders();
}
