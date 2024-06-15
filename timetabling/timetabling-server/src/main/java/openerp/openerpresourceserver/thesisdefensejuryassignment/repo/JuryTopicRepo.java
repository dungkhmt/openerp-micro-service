package openerp.openerpresourceserver.thesisdefensejuryassignment.repo;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuryTopicRepo extends JpaRepository<JuryTopic, Integer> {
}
