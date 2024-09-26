package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductFacility;

public interface ProductFacilityRepo extends JpaRepository<ProductFacility, Long> {
    @Query(value = "select * from scm_product_facility where facility_code = :facilityCode", nativeQuery = true)
    Page<ProductFacility> search(Pageable pageable, String facilityCode);
    @Query(value = "select * from scm_product_facility where facility_code = :facilityCode and product_code = :productCode", nativeQuery = true)
    ProductFacility findInventoryInFacility(String facilityCode, String productCode);
}
