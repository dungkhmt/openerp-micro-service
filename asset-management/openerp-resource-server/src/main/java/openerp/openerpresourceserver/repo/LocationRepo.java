package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepo extends JpaRepository<Location, Integer> {
}
