package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.DeliveryBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryBillRepository extends JpaRepository<DeliveryBill, String> {

    List<DeliveryBill> findAllByDeliveryTripId(String deliveryTripId);

    List<DeliveryBill> findAllByOrderByCreatedStampDesc();

}
