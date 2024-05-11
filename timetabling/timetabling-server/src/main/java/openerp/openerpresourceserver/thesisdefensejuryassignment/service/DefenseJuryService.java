package openerp.openerpresourceserver.thesisdefensejuryassignment.service;

import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.*;

import java.util.List;
import java.util.UUID;


public interface DefenseJuryService {
    public DefenseJury createNewDefenseJury(DefenseJuryIM defenseJury);

    public List<Teacher> getAllTeachers();

    public DefenseJury getDefenseJuryByID(UUID id);

    public List<Thesis> getAllAvailableThesiss(String thesisDefensePlanId);

    public DefenseJury assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList);

    public DefenseJury assignReviewerToThesis(AssignReviewerToThesisIM teacherAndThesisList);
    public String assignTeacherAndThesisAutomatically(AssignTeacherToDefenseJuryAutomaticallyIM teacherIdList);

    public int updateDefenseJury(UpdateDefenseJuryIM updateDefenseJuryIM);
}
