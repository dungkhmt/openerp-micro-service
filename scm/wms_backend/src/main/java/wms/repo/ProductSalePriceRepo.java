package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import wms.entity.ProductSalePrice;

import java.util.List;

public interface ProductSalePriceRepo extends JpaRepository<ProductSalePrice, Long> {
    @Query(value = "select * from scm_product_price_sellout", nativeQuery = true)
    List<ProductSalePrice> getAll();
    @Query(value = "select * from scm_product_price_sellout where product_code = :productCode and contract_type_code = :contractCode", nativeQuery = true)
    ProductSalePrice getByProductAndContract(String productCode, String contractCode);
}
