package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDefensePlanDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.JuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateJuryTopicIM;

import java.util.List;

public interface JuryTopicService {
    List<JuryTopic> getAll();

    String createJuryTopic(JuryTopicIM juryTopicIM);

    String updateJuryTopic(int juryTopicId, UpdateJuryTopicIM updateJuryTopicIM);
    List<JuryTopic> getAllByThesisDefensePlanId(String thesisDefensePlanId);

    List<ThesisDefensePlanDTO> getAllThesisDefensePlan();
}
