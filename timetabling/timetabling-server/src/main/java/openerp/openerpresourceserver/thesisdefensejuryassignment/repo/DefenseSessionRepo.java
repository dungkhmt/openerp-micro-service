package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseSession;

@Repository
public interface DefenseSessionRepo extends JpaRepository<DefenseSession, Integer> {
}
