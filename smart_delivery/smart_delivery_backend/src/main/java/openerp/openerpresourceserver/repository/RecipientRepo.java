package openerp.openerpresourceserver.repository;

import openerp.openerpresourceserver.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipientRepo extends JpaRepository<Recipient, UUID> {
    boolean existsByName(String senderName);

    Recipient findByName(String senderName);

    Recipient findByNameAndPhone(String recipientName, String recipientPhone);
}
