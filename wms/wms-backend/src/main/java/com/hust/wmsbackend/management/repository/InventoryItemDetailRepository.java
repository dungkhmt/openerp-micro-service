package com.hust.wmsbackend.management.repository;

import com.hust.wmsbackend.management.entity.InventoryItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryItemDetailRepository extends JpaRepository<InventoryItemDetail, UUID> {
}
