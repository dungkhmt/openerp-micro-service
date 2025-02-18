package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.InventoryItemDetail;

@Repository
public interface InventoryItemDetailRepository extends JpaRepository<InventoryItemDetail, UUID> {
}

