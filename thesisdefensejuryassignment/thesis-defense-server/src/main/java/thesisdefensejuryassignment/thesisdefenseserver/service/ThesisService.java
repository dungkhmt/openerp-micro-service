package thesisdefensejuryassignment.thesisdefenseserver.service;

import thesisdefensejuryassignment.thesisdefenseserver.dto.ThesisDTO;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Thesis;
import thesisdefensejuryassignment.thesisdefenseserver.entity.TrainingProgram;
import thesisdefensejuryassignment.thesisdefenseserver.models.ThesisModel;

import java.util.List;
import java.util.Optional;

public interface ThesisService {
    Thesis createNewThesis(ThesisModel thesisModel);

    List<TrainingProgram> getAllTrainingProgram();

    List<ThesisDTO> getAllByStudentEmail(String studentEmail);

}
