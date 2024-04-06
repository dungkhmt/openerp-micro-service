package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CVWorkingExperience;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CVWorkingExperienceRepo  extends JpaRepository<CVWorkingExperience, Integer> {
    List<CVWorkingExperience> findBycvId(Integer cvId);
}
