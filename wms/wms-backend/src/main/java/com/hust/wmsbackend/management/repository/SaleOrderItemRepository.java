package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.SaleOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleOrderItemRepository extends JpaRepository<SaleOrderItem, UUID> {

    List<SaleOrderItem> findAllByOrderId(UUID orderId);

    Optional<SaleOrderItem> findSaleOrderItemByOrderIdAndProductId(UUID orderId, UUID productId);

    List<SaleOrderItem> findAllByProductId(UUID productId);
}
