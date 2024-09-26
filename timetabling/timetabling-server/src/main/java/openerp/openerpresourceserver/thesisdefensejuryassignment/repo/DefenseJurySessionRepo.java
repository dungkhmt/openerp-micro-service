package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJurySession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefenseJurySessionRepo extends JpaRepository<DefenseJurySession, Integer> {
}
