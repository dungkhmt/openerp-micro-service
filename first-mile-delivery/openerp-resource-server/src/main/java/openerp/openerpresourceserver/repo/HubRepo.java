package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepo extends JpaRepository<Hub, UUID> {

    List<Hub> findAll();

    Optional<Hub> findById(UUID id);

    Hub save(Hub hub);

    void deleteById(UUID id);}
