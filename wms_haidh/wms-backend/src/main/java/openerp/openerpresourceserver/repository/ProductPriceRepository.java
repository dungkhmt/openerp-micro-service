package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import openerp.openerpresourceserver.entity.ProductPrice;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {
    Page<ProductPrice> findByProductId(UUID productId, Pageable pageable);
}

