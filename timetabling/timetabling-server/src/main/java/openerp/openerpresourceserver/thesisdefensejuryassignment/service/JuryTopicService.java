package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.JuryTopicIM;

import java.util.List;

public interface JuryTopicService {
    List<JuryTopic> getAll();
    JuryTopic getById(int id);

    String createJuryTopic(JuryTopicIM juryTopicIM);
}
