package com.hust.baseweb.applications.education.service;

import java.util.List;
import java.util.UUID;

import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourseSession;

public interface EduCourseSessionService {
    public EduCourseSession createCourseSession(String courseId, String sessionName, String createdByUserLoginId, String description);
}
