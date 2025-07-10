package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.DeliveryTripPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryTripPathRepository extends JpaRepository<DeliveryTripPath, Long> {

    void deleteAllByDeliveryTripId(String deliveryTripId);

    List<DeliveryTripPath> findAllByDeliveryTripId(String deliveryTripId);

}
