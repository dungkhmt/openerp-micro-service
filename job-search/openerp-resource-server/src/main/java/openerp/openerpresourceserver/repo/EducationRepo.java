package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Education;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRepo  extends JpaRepository<Education, Integer> {
    List<Education> findByUserId(String userId);
}
