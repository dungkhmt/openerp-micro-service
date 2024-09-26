package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HubRepo extends JpaRepository<Hub, UUID> {
}
