package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductCategory;

public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Long> {
    @Query(value = "select * from product_category", nativeQuery = true)
    Page<ProductCategory> search(Pageable pageable);

    ProductCategory getProductCategoryById(long id);
    ProductCategory getProductCategoryByCode(String code);
}
