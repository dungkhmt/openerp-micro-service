package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SenderRepo extends JpaRepository<Sender, UUID> {
    Sender findByName(String name);

    boolean existsByName(String name);

    Sender findByNameAndPhone(String senderName, String senderPhone);
}
