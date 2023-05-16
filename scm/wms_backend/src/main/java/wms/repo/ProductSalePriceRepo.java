package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ProductPrice;
import wms.entity.ProductSalePrice;

public interface ProductSalePriceRepo extends JpaRepository<ProductSalePrice, Long> {

}
