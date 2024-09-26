package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourseSession;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EduCourseSessionRepo extends JpaRepository<EduCourseSession, UUID> {
    public List<EduCourseSession> findByCourseId(String courseId);
}
