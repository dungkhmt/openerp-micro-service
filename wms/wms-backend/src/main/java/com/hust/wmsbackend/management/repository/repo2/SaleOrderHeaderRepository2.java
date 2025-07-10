package com.hust.wmsbackend.management.repository.repo2;

import com.hust.wmsbackend.management.entity.SaleOrderHeader;
import com.hust.wmsbackend.management.entity.enumentity.OrderStatus;
import com.hust.wmsbackend.management.model.model2.OrderDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface SaleOrderHeaderRepository2 extends JpaRepository<SaleOrderHeader, UUID> {

    List<SaleOrderHeader> findAllByOrderByOrderDateDesc();

    List<SaleOrderHeader> findAllByStatusInOrderByOrderDateDesc(List<OrderStatus> orderStatuses);

    @Query("SELECT new com.hust.wmsbackend.management.model.model2.OrderDetailDTO(" +
            "soh.orderId, soh.userLoginId, soh.customerName, soh.paymentType, soh.status, " +
            "soh.createdStamp, soh.totalOrderCost, " +
            "ca.addressName, ca.longitude, ca.latitude) " +
            "FROM SaleOrderHeader soh " +
            "JOIN CustomerAddress ca ON soh.customerAddressId = ca.customerAddressId " +
            "WHERE soh.orderId = :orderId")
    OrderDetailDTO findAllBasicInfoOfOrderByOrderId(@Param("orderId") UUID orderId);

}
