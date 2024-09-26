package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Experience;
import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ExperienceRepo  extends JpaRepository<Experience, Integer> {
    List<Experience> findByUserId(String userId);
}
