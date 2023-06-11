package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.SaleOrderHeader;
import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaleOrderHeaderRepository extends JpaRepository<SaleOrderHeader, UUID> {

    List<SaleOrderHeader> findAllByOrderByOrderDateDesc();

    List<SaleOrderHeader> findAllByStatusInOrderByOrderDateDesc(List<OrderStatus> orderStatuses);

}
