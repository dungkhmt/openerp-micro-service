package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import openerp.openerpresourceserver.dto.request.AddressDistanceDTO;
import openerp.openerpresourceserver.dto.response.AddressDistanceResponse;
import openerp.openerpresourceserver.entity.AddressDistance;
import openerp.openerpresourceserver.entity.AddressType;

@Repository
public interface AddressDistanceRepository extends JpaRepository<AddressDistance, UUID> {

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.AddressDistanceResponse(
			     ad.addressDistanceId,
			     w.name,
			     c.addressName,
			     ad.distance
			 )
			    FROM AddressDistance ad
			    JOIN Warehouse w ON ad.fromLocationId = w.warehouseId
			    JOIN CustomerAddress c ON ad.toLocationId = c.customerAddressId
			    WHERE ad.fromLocationType = 'WAREHOUSE'
			      AND ad.toLocationType = 'CUSTOMER'
			      AND (:fromLocation IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(c.addressName) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceResponse> findWarehouseToCustomer(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.response.AddressDistanceResponse(ad.addressDistanceId,
			           c.addressName,
			           w.name,
			           ad.distance)
			    FROM AddressDistance ad
			    JOIN CustomerAddress c ON ad.fromLocationId = c.customerAddressId
			    JOIN Warehouse w ON ad.toLocationId = w.warehouseId
			    WHERE ad.fromLocationType = 'CUSTOMER'
			      AND ad.toLocationType = 'WAREHOUSE'
			      AND (:fromLocation IS NULL OR LOWER(c.addressName) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceResponse> findCustomerToWarehouse(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

	@Query("""
			     SELECT new openerp.openerpresourceserver.dto.response.AddressDistanceResponse(ad.addressDistanceId,
			           c1.addressName,
			           c2.addressName,
			           ad.distance)
			    FROM AddressDistance ad
			    JOIN CustomerAddress c1 ON ad.fromLocationId = c1.customerAddressId
			    JOIN CustomerAddress c2 ON ad.toLocationId = c2.customerAddressId
			    WHERE ad.fromLocationType = 'CUSTOMER'
			      AND ad.toLocationType = 'CUSTOMER'
			      AND (:fromLocation IS NULL OR LOWER(c1.addressName) LIKE LOWER(CONCAT('%', :fromLocation, '%')))
			      AND (:toLocation IS NULL OR LOWER(c2.addressName) LIKE LOWER(CONCAT('%', :toLocation, '%')))
			""")
	Page<AddressDistanceResponse> findCustomerToCustomer(@Param("fromLocation") String fromLocation,
			@Param("toLocation") String toLocation, Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE AddressDistance ad SET ad.distance = :distance, ad.lastUpdatedStamp = CURRENT_TIMESTAMP WHERE ad.addressDistanceId = :id")
	int updateDistanceById(@Param("id") UUID id, @Param("distance") double distance);

	@Query("""
			SELECT new openerp.openerpresourceserver.dto.request.AddressDistanceDTO(ad.addressDistanceId,
			       new openerp.openerpresourceserver.dto.request.CoordinateDTO(w.longitude, w.latitude),
			       new openerp.openerpresourceserver.dto.request.CoordinateDTO(c.longitude, c.latitude))
			       FROM AddressDistance ad
			       JOIN Warehouse w ON ad.fromLocationId = w.warehouseId AND ad.fromLocationType = 'WAREHOUSE'
			       JOIN CustomerAddress c ON ad.toLocationId = c.customerAddressId AND ad.toLocationType = 'CUSTOMER'
			       WHERE ad.distance = 0
			        """)
	List<AddressDistanceDTO> findWarehouseToCustomerWithDistanceZero();

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.request.AddressDistanceDTO(ad.addressDistanceId,
			           new openerp.openerpresourceserver.dto.request.CoordinateDTO(c.longitude, c.latitude),
			           new openerp.openerpresourceserver.dto.request.CoordinateDTO(w.longitude, w.latitude))
			           FROM AddressDistance ad
			           JOIN CustomerAddress c ON ad.fromLocationId = c.customerAddressId AND ad.fromLocationType = 'CUSTOMER'
			           JOIN Warehouse w ON ad.toLocationId = w.warehouseId AND ad.toLocationType = 'WAREHOUSE'
			           WHERE ad.distance = 0
			""")
	List<AddressDistanceDTO> findCustomerToWarehouseWithDistanceZero();

	@Query("""
			    SELECT new openerp.openerpresourceserver.dto.request.AddressDistanceDTO(ad.addressDistanceId,
			           new openerp.openerpresourceserver.dto.request.CoordinateDTO(c1.longitude, c1.latitude),
			           new openerp.openerpresourceserver.dto.request.CoordinateDTO(c2.longitude, c2.latitude))
			           FROM AddressDistance ad
			           JOIN CustomerAddress c1 ON ad.fromLocationId = c1.customerAddressId AND ad.fromLocationType = 'CUSTOMER'
			           JOIN CustomerAddress c2 ON ad.toLocationId = c2.customerAddressId AND ad.toLocationType = 'CUSTOMER'
			           WHERE ad.distance = 0
			""")
	List<AddressDistanceDTO> findCustomerToCustomerWithDistanceZero();

	List<AddressDistance> findByFromLocationTypeAndToLocationType(AddressType fromType, AddressType toType);

}
