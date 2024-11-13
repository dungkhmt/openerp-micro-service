package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EduCourseSessionInteractiveQuizRepo extends JpaRepository<EduCourseSessionInteractiveQuiz, UUID> {
    List<EduCourseSessionInteractiveQuiz> findBySessionId(UUID sessionId);
}
