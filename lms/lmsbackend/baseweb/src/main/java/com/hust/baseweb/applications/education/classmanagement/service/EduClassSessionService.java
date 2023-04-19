package com.hust.baseweb.applications.education.classmanagement.service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.model.EduClassSessionDetailOM;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;

import java.util.List;
import java.util.UUID;

public interface EduClassSessionService {
    EduClassSession save(UUID classId, String sessionName, String description, String userLoginId);
    List<EduClassSession> findAllByClassId(UUID classId);

    EduQuizTest createQuizTestOfClassSession(UUID sessionId, String testId, String testName, int duration);

    List<EduQuizTest> findAllBySession(UUID sessionId);

    EduClassSessionDetailOM getSessionDetail(UUID sessionId);
}
