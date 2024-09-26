package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CVSkill;
import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CVSkillRepo  extends JpaRepository<CVSkill, Integer> {
    List<CVSkill> findBycvId(Integer cvId);
}
