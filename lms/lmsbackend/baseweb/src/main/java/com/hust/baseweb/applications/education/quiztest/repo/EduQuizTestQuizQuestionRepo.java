package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeEduQuizTestQuizQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EduQuizTestQuizQuestionRepo extends JpaRepository<EduQuizTestQuizQuestion, CompositeEduQuizTestQuizQuestionId> {
    public List<EduQuizTestQuizQuestion> findAllByTestId(String testId);
    public List<EduQuizTestQuizQuestion> findAllByTestIdAndStatusId(String testId, String statusId);
    public EduQuizTestQuizQuestion findByTestIdAndQuestionId(String testId, UUID questionId);
    public List<EduQuizTestQuizQuestion> findAllByQuestionId(UUID questionId);
}
