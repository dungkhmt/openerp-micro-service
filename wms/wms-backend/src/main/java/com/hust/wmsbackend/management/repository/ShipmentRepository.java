package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {

    List<Shipment> findAllByIsDeletedIsFalseOrderByCreatedStampDesc();

}
