package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.CustomerType;

public interface CustomerTypeRepo  extends JpaRepository<CustomerType, Long> {
    @Query(value = "select * from scm_customer_type where is_deleted = 0", nativeQuery = true)
    Page<CustomerType> search(Pageable pageable);

    CustomerType getCustomerTypeById(long id);
    CustomerType getCustomerTypeByCode(String code);
}
