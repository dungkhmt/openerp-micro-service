package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.UsernameHubId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsernameHubIdRepo extends JpaRepository<UsernameHubId, UUID> {
    Optional<UsernameHubId> findByUserName(String userName);
} 