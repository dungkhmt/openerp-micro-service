package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroupParticipationAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroup.QuizTestGroupParticipantAssignmentOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.AddParticipantToQuizTestGroupInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.ModelResponseGetQuizTestGroup;
import com.hust.baseweb.applications.education.quiztest.model.quiztestgroupparticipant.RemoveParticipantToQuizTestGroupInputModel;

import java.util.List;

public interface EduTestQuizGroupParticipationAssignmentService {

    public List<QuizTestGroupParticipantAssignmentOutputModel> getQuizTestGroupParticipant(String testId);

    public EduTestQuizGroupParticipationAssignment assignParticipant2QuizTestGroup(
        AddParticipantToQuizTestGroupInputModel input
    );

    public boolean removeParticipantFromQuizTestGroup(RemoveParticipantToQuizTestGroupInputModel input);

    public ModelResponseGetQuizTestGroup getQuizTestGroupOfUser(String userId, String testId);
}
