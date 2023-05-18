package wms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import wms.entity.ProductPrice;

public interface ProductPriceRepo extends JpaRepository<ProductPrice, Long> {
}
