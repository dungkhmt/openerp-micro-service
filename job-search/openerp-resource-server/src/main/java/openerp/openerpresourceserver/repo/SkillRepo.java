package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepo extends JpaRepository<Skill, Integer> {
    List<Skill> findByUserId(String userId);
}
