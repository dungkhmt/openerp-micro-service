package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.embedded.DefenseJuryTeacherRole;

import java.util.List;
import java.util.UUID;

@Repository
public interface DefenseJuryTeacherRoleRepo extends JpaRepository<DefenseJuryTeacherRole, Integer> {
    List<DefenseJuryTeacherRole> findAllByDefenseJuryAndTeacherAndRole(UUID juryId, UUID teacherId, int roleId);
}
