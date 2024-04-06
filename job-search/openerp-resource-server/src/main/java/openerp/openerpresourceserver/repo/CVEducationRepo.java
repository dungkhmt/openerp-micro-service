package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CVEducation;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CVEducationRepo  extends JpaRepository<CVEducation, Integer> {
    public List<CVEducation> findBycvId(Integer cvId);
}
