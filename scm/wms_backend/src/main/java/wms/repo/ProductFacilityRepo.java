package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.Facility;
import wms.entity.ProductFacility;

public interface ProductFacilityRepo extends JpaRepository<ProductFacility, Long> {
    @Query(value = "select * from product_facility where facility_code = :facilityCode", nativeQuery = true)
    Page<ProductFacility> search(Pageable pageable, String facilityCode);
}
