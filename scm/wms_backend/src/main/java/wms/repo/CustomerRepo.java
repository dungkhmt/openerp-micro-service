package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Customer;

import java.util.List;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    @Query(value = "select * from scm_customer", nativeQuery = true)
    Page<Customer> search(Pageable pageable);
    Customer getCustomerById(long id);
    Customer getCustomerByCode(String code);
    @Query(value = "select * from scm_customer", nativeQuery = true)
    List<Customer> getAllCustomers();
}
