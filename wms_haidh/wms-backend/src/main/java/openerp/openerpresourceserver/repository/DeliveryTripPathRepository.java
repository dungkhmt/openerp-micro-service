package openerp.openerpresourceserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import openerp.openerpresourceserver.dto.request.CoordinateDTO;
import openerp.openerpresourceserver.entity.DeliveryTripPath;

public interface DeliveryTripPathRepository extends JpaRepository<DeliveryTripPath, Integer> {

	@Query("SELECT new openerp.openerpresourceserver.dto.request.CoordinateDTO(dtp.longitude, dtp.latitude) " +
		       "FROM DeliveryTripPath dtp WHERE dtp.deliveryTripId = :deliveryTripId")
		List<CoordinateDTO> findByDeliveryTripId(@Param("deliveryTripId") String deliveryTripId);

}
