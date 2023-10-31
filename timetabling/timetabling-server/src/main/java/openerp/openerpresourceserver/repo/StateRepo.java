package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepo extends JpaRepository<State, Long> {
}
