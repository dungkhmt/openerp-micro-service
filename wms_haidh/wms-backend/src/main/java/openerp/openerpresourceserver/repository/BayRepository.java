package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Bay;
import openerp.openerpresourceserver.entity.projection.BayProjection;

@Repository
public interface BayRepository extends JpaRepository<Bay, UUID> {
    @Query("SELECT b.bayId AS bayId, b.code AS code FROM Bay b WHERE b.warehouseId = :warehouseId")
    List<BayProjection> findByWarehouseIdWithProjection(@Param("warehouseId") UUID warehouseId);
}
