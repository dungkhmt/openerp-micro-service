package openerp.openerpresourceserver.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    // Lấy tất cả danh sách warehouse
    List<Warehouse> findAll();
}

