package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuizQuestion;
import com.hust.baseweb.applications.education.entity.compositeid.CompositeCourseSessionInteractiveQuizQuestionId;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EduCourseSessionInteractiveQuizQuestionRepo extends JpaRepository<EduCourseSessionInteractiveQuizQuestion, CompositeCourseSessionInteractiveQuizQuestionId> {
    public List<EduCourseSessionInteractiveQuizQuestion> findByInteractiveQuizId(UUID interactiveQuizId);
}
