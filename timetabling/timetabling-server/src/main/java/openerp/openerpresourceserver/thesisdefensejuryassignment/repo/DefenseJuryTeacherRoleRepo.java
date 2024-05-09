package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;
@Repository
public interface DefenseJuryTeacherRoleRepo extends JpaRepository<DefenseJuryTeacherRole, Integer> {
}
