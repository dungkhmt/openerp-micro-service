package openerp.openerpresourceserver.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import openerp.openerpresourceserver.entity.DeliveryPerson;
import openerp.openerpresourceserver.entity.projection.DeliveryPersonProjection;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, String> {

	@Query("SELECT d.userLoginId AS userLoginId, d.fullName AS fullName, d.phoneNumber AS phoneNumber FROM DeliveryPerson d")
	List<DeliveryPersonProjection> findAllDeliveryPersons();

	Page<DeliveryPerson> findByFullNameContainingIgnoreCase(String search, Pageable pageable);
	
}
