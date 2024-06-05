package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseRoom;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseSession;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DefenseJuryRepo extends JpaRepository<DefenseJury, UUID> {
    List<DefenseJury> findByThesisDefensePlanAndDefenseDateAndDefenseSessionAndDefenseJuryTeacherRolesTeacherId(ThesisDefensePlan thesisDefensePlan,
                                                                                                                Date defenseDate,
                                                                                                                DefenseSession defenseSession,
                                                                                                                String defenseJuryTeacherRoleTeacherId
                                                                                                );
    List<DefenseJury> findByThesisDefensePlanAndDefenseDateAndDefenseSessionAndDefenseRoom(ThesisDefensePlan thesisDefensePlan,
                                                                                           Date defenseDate,
                                                                                           DefenseSession defenseSession,
                                                                                           DefenseRoom defenseRoom);
}
