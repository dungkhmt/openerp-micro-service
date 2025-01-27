package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.EduTestQuizParticipationCreateInputModel;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.ModelResponseImportExcelUsersToQuizTest;
import com.hust.baseweb.entity.UserLogin;

import java.util.List;

public interface EduTestQuizParticipantService {

    public EduTestQuizParticipant register(UserLogin userLogin, EduTestQuizParticipationCreateInputModel input);

    public EduTestQuizParticipant findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(
        String userId,
        String testId
    );

    public boolean addParticipant2QuizTest(String userId, String testId);
    public int addParticipants2QuizTest(String testId, List<ModelResponseImportExcelUsersToQuizTest> L);

}
