package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizQuestionId;
import com.hust.baseweb.applications.education.quiztest.model.InteractiveQuizQuestionInputModel;

import java.util.List;
import java.util.UUID;

public interface InteractiveQuizQuestionService {
    public List<QuizQuestionDetailModel> findAllByInteractiveQuizId(UUID interactiveQuizId);
    public void removeFromInteractiveQuiz(InteractiveQuizQuestionInputModel input);
}
