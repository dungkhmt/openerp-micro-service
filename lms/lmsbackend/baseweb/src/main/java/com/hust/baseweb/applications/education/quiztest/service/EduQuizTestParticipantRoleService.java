package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizRole;
import com.hust.baseweb.applications.education.quiztest.model.ModelCreateEduQuizTestParticipantRole;
import com.hust.baseweb.applications.education.quiztest.model.QuizTestParticipantRoleModel;

import java.util.List;

public interface EduQuizTestParticipantRoleService {
    EduTestQuizRole save(ModelCreateEduQuizTestParticipantRole input);
    List<QuizTestParticipantRoleModel> getParticipantRolesOfQuizTest(String testId);
    List<QuizTestParticipantRoleModel> getQuizTestsOfUser(String userId);
    List<QuizTestParticipantRoleModel> getAllQuizTests();

    List<QuizTestParticipantRoleModel> getParticipantRolesOfUserInQuizTest(String userId, String testId);

    void deleteParticipantRole(String testId, String participantId, String role);
}
