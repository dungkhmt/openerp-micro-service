package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status, Integer> {
}
