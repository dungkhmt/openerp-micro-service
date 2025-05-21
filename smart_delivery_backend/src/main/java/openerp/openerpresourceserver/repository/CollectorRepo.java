package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Collector;
import openerp.openerpresourceserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CollectorRepo extends JpaRepository<Collector, UUID> {
    @Query("SELECT c FROM Collector c WHERE c.hubId = :hubId")
    List<Collector> getAllByHubId(UUID hubId);

    Collector findByEmail(String email);

    Collector findByUsername(String username);

    List<Collector> findAllByHubId(UUID hubId);
}
