package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Bay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BayRepo extends JpaRepository<Bay, UUID> {
    @Query("SELECT b FROM Bay b WHERE b.hub.id = :hubId")
    List<Bay> findByHubId(@Param("hubId") UUID hubId);

}
