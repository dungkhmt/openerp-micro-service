package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;

import java.util.List;
import java.util.UUID;

public interface InteractiveQuizQuestionService {
    public List<QuizQuestionDetailModel> findAllByInteractiveQuizId(UUID interactiveQuizId);
}
