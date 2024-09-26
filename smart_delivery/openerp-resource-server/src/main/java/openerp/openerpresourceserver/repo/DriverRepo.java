package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DriverRepo extends JpaRepository<Driver, UUID> {
}
