package thesisdefensejuryassignment.thesisdefenseserver.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thesisdefensejuryassignment.thesisdefenseserver.entity.embedded.DefenseJuryTeacherRole;
@Repository
public interface DefenseJuryTeacherRoleRepo extends JpaRepository<DefenseJuryTeacherRole, Integer> {
}
