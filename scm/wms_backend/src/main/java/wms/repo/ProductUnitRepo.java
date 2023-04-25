package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductUnit;

public interface ProductUnitRepo extends JpaRepository<ProductUnit, Long> {
    @Query(value = "select * from product_unit", nativeQuery = true)
    Page<ProductUnit> search(Pageable pageable);

    ProductUnit getProductUnitById(long id);
    ProductUnit getProductUnitByCode(String code);
}