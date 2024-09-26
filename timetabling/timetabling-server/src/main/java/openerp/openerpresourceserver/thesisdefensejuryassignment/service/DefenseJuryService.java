package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.*;

import java.util.List;
import java.util.UUID;


public interface DefenseJuryService {
    public String createNewDefenseJury(DefenseJuryIM defenseJury);

    public List<Teacher> getAllTeachers();

    public DefenseJuryDTO getDefenseJuryByID(UUID id);

    public List<ThesisDTO> getAllAvailableThesiss(String thesisDefensePlanId);

    public String assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList);

    public DefenseJury assignReviewerToThesis(AssignReviewerToThesisIM teacherAndThesisList);
    public List<Teacher> assignTeacherAutomatically(String thesisDefensePlanId, String defenseJuryId, AssignTeacherToDefenseJuryAutomaticallyIM thesisList);

    public String updateDefenseJury(UpdateDefenseJuryIM updateDefenseJuryIM);

    public DefenseJury reassignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList);

    public DefenseJury deleteDefenseJuryByID(UUID id);

    public List<Thesis> getAvailableThesisByJuryTopic(String thesisDefensePlanId, String defenseJuryId);

}
