package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<Request, Integer> {
}
