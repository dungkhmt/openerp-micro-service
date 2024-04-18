package thesisdefensejuryassignment.thesisdefenseserver.service;

import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Teacher;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Thesis;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignReviewerToThesisIM;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignTeacherAndThesisToDefenseJuryIM;
import thesisdefensejuryassignment.thesisdefenseserver.models.DefenseJuryIM;

import java.util.List;
import java.util.UUID;


public interface DefenseJuryService {
    public DefenseJury createNewDefenseJury(DefenseJuryIM defenseJury);

    public List<Teacher> getAllTeachers();

    public DefenseJury getDefenseJuryByID(UUID id);

    public List<Thesis> getAllAvailableThesiss(String thesisDefensePlanId);

    public DefenseJury assignTeacherAndThesis(AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList);

    public DefenseJury assignReviewerToThesis(AssignReviewerToThesisIM teacherAndThesisList);

}
