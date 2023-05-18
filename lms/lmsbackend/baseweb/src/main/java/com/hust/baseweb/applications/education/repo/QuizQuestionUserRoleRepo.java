package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.QuizQuestionUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionUserRoleRepo extends JpaRepository<QuizQuestionUserRole, UUID> {

    List<QuizQuestionUserRole> findAllByUserId(String userId);

    List<QuizQuestionUserRole> findAllByQuestionId(UUID questionId);

    List<QuizQuestionUserRole> findAllByQuestionIdAndUserId(UUID questionId, String userId);
}
