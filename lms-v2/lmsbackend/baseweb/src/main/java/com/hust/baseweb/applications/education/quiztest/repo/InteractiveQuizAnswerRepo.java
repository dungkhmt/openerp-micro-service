package com.hust.baseweb.applications.education.quiztest.repo;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizAnswer;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizAnswerId;

public interface InteractiveQuizAnswerRepo extends JpaRepository<InteractiveQuizAnswer, CompositeInteractiveQuizAnswerId> {
    public List<InteractiveQuizAnswer> findAllByInteractiveQuizIdAndQuestionIdAndUserId(UUID interactiveQuizId, UUID questionId, String userId);
    public List<InteractiveQuizAnswer> findByInteractiveQuizId(UUID interactiveQuizId);

    public List<InteractiveQuizAnswer> findAllByInteractiveQuizIdAndQuestionId(UUID interactiveQuizId, UUID questionId);
}
