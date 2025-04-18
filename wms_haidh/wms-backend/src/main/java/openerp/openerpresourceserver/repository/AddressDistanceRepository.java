package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.AddressDistance;
import openerp.openerpresourceserver.projection.AddressDistanceProjection;

@Repository
public interface AddressDistanceRepository extends JpaRepository<AddressDistance, UUID> {

	@Query("""
			    SELECT ad.addressDistanceId AS addressDistanceId,
			           w.name AS fromLocationName,
			           c.addressName AS toLocationName,
			           ad.distance AS distance
			    FROM AddressDistance ad
			    JOIN Warehouse w ON ad.fromLocationId = w.warehouseId
			    JOIN CustomerAddress c ON ad.toLocationId = c.customerAddressId
			    WHERE ad.fromLocationType = 'WAREHOUSE'
			      AND ad.toLocationType = 'CUSTOMER'
			      AND (:fromLocation IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(c.addressName) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceProjection> findWarehouseToCustomer(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

	@Query("""
			    SELECT ad.addressDistanceId AS addressDistanceId,
			           c.addressName AS fromLocationName,
			           w.name AS toLocationName,
			           ad.distance AS distance
			    FROM AddressDistance ad
			    JOIN CustomerAddress c ON ad.fromLocationId = c.customerAddressId
			    JOIN Warehouse w ON ad.toLocationId = w.warehouseId
			    WHERE ad.fromLocationType = 'CUSTOMER'
			      AND ad.toLocationType = 'WAREHOUSE'
			      AND (:fromLocation IS NULL OR LOWER(c.addressName) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceProjection> findCustomerToWarehouse(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

	@Query("""
			    SELECT ad.addressDistanceId AS addressDistanceId,
			           c1.addressName AS fromLocationName,
			           c2.addressName AS toLocationName,
			           ad.distance AS distance
			    FROM AddressDistance ad
			    JOIN CustomerAddress c1 ON ad.fromLocationId = c1.customerAddressId
			    JOIN CustomerAddress c2 ON ad.toLocationId = c2.customerAddressId
			    WHERE ad.fromLocationType = 'CUSTOMER'
			      AND ad.toLocationType = 'CUSTOMER'
			      AND (:fromLocation IS NULL OR LOWER(c1.addressName) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(c2.addressName) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceProjection> findCustomerToCustomer(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

}
