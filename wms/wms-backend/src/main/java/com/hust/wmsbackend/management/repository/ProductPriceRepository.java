package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllByProductId(UUID productId);

}
