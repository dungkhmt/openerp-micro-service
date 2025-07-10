package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JuryTopicRepo extends JpaRepository<JuryTopic, Integer> {
    List<JuryTopic> findByThesisDefensePlanId(String thesisDefensePlanId);
}
