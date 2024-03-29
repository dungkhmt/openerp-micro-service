package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {



    Optional<Customer> findFirstByUserId(@Param("userId") String userId);

}
