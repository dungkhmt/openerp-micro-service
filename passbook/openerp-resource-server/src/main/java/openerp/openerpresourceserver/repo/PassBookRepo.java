package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.PassBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PassBookRepo extends JpaRepository<PassBook, UUID> {
    List<PassBook> findAllByUserId(String userId);
}
