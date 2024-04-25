package com.hust.baseweb.applications.education.service;

import java.util.List;
import java.util.UUID;

import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;

public interface EduCourseSessionInteractiveQuizService {
    public EduCourseSessionInteractiveQuiz createCourseSessionInteractiveQuiz(String interactiveQuizName, UUID sessionId, String description);
}
