package wms.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductEntity;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    @Query(value = "select * from scm_product", nativeQuery = true)
    Page<ProductEntity> search(Pageable pageable);

    ProductEntity getProductById(long id);
    ProductEntity getProductByCode(String code);
    ProductEntity getProductBySku(String sku);
}
