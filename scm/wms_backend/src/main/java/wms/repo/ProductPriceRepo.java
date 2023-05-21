package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductPrice;

import java.util.List;

public interface ProductPriceRepo extends JpaRepository<ProductPrice, Long> {
    @Query(value = "select * from scm_product_price_sellin", nativeQuery = true)
    List<ProductPrice> getAll();
    @Query(value = "select * from scm_product_price_sellin where product_code = :productCode", nativeQuery = true)
    ProductPrice getByProductCode(String productCode);
}
