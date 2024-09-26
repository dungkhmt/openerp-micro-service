package com.hust.baseweb.applications.education.service;

import java.util.List;
import java.util.UUID;

import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;

public interface EduCourseSessionInteractiveQuizQuestionService {
    public List<QuizQuestionDetailModel> findAllByInteractiveQuizId(UUID interactiveQuizId);
}
