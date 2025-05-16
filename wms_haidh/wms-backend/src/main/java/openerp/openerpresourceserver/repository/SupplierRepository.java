package openerp.openerpresourceserver.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import openerp.openerpresourceserver.entity.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

	Page<Supplier> findByNameContainingIgnoreCase(String search, Pageable pageable);
}

