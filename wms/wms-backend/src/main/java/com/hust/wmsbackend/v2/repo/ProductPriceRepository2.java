package com.hust.wmsbackend.v2.repo;

import com.hust.wmsbackend.management.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductPriceRepository2 extends JpaRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllByProductId(UUID productId);

    @Query(value = "SELECT p FROM ProductPrice p WHERE p.productId = :productId AND p.startDate <= :now AND (p.endDate IS NULL OR p.endDate >= :now)")
    List<ProductPrice> findCurrentPriceByProductId(UUID productId, Date now);
}
