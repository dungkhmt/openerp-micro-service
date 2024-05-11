package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.TrainingProgram;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisModel;

import java.util.List;
import java.util.Optional;

public interface ThesisService {
    Thesis createNewThesis(ThesisModel thesisModel);

    List<TrainingProgram> getAllTrainingProgram();

    List<ThesisDTO> getAllByStudentEmail(String studentEmail);

}
