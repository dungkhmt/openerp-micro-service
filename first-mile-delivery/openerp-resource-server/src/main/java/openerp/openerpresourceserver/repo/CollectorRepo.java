package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Collector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CollectorRepo extends JpaRepository<Collector, UUID> {

    List<Collector> findByHubId(UUID hubId);

}
