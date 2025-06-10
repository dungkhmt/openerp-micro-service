package openerp.openerpresourceserver.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import openerp.openerpresourceserver.dto.response.DeliveryPersonResponse;
import openerp.openerpresourceserver.entity.DeliveryPerson;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, String> {

	@Query("SELECT new openerp.openerpresourceserver.dto.response.DeliveryPersonResponse(d.userLoginId, d.fullName) "
			+ "FROM DeliveryPerson d WHERE d.status = 'AVAILABLE'")
	List<DeliveryPersonResponse> findAllAvailableDeliveryPersons();

	Page<DeliveryPerson> findByFullNameContainingIgnoreCaseAndStatus(String fullName, String status, Pageable pageable);

}
