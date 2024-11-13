package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizChoiceAnswerRepo extends JpaRepository<QuizChoiceAnswer, UUID> {

    List<QuizChoiceAnswer> findAllByQuizQuestion(QuizQuestion quizQuestion);
}
