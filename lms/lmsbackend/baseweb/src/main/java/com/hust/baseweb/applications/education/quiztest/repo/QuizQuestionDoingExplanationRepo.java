package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.QuizQuestionDoingExplanation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.UUID;

public interface QuizQuestionDoingExplanationRepo extends JpaRepository<QuizQuestionDoingExplanation, UUID> {

    Collection<QuizQuestionDoingExplanation> findByParticipantUserIdAndQuestionId(String userId, UUID questionId, Sort sort);
}
