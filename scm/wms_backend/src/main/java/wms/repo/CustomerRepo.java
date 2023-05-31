package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Customer;

import java.util.List;

public interface CustomerRepo extends JpaRepository<Customer, Long> {
    @Query(value = "select * from scm_customer where is_deleted = 0", nativeQuery = true)
    Page<Customer> search(Pageable pageable);
    Customer getCustomerById(long id);
    Customer getCustomerByCode(String code);
    @Query(value = "select * from scm_customer where is_deleted = 0", nativeQuery = true)
    List<Customer> getAllCustomers();
    @Query(value = "select * from scm_customer where is_deleted = 0 and facility_code = :facilityCode", nativeQuery = true)
    List<Customer> getFacilityCustomers(String facilityCode);
    @Query(value = "select * from scm_customer where is_deleted = 0 and facility_code = :facilityCode", nativeQuery = true)
    Page<Customer> getFacilityCustomersPaging(Pageable pageable ,String facilityCode);
}
