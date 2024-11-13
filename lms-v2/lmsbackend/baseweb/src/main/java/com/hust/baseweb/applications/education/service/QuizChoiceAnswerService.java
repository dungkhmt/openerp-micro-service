package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.model.quiz.QuizChoiceAnswerCreateInputModel;

import java.util.List;
import java.util.UUID;

public interface QuizChoiceAnswerService {

    List<QuizChoiceAnswer> findAll();

    QuizChoiceAnswer save(QuizChoiceAnswerCreateInputModel input);

    List<QuizChoiceAnswer> findAllByQuizQuestionId(UUID quizQuestionId);

    QuizChoiceAnswer findById(UUID choiceAnswerId);

    QuizChoiceAnswer update(UUID choiceAnswerId, QuizChoiceAnswerCreateInputModel input);

    QuizChoiceAnswer delete(UUID choiceAnswerId);
}
