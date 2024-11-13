package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizQuestionId;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractiveQuizQuestionRepo extends JpaRepository<InteractiveQuizQuestion, CompositeInteractiveQuizQuestionId> {
        public List<InteractiveQuizQuestion> findAllByInteractiveQuizId(UUID interactiveQuizId);
        public List<InteractiveQuizQuestion> findAllByInteractiveQuizIdIn(Set<UUID> interactiveQuizIds);

}
