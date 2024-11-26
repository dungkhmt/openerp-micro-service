package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.education.entity.EduClass;
import com.hust.baseweb.applications.education.entity.EduCourseSession;
import com.hust.baseweb.applications.education.repo.EduCourseSessionRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduCourseSessionServiceImpl implements EduCourseSessionService{
    private EduCourseSessionRepo eduCourseSessionRepo;
    private EduClassSessionRepo eduClassSessionRepo;
    @Override
    public EduCourseSession createCourseSession(String courseId, String sessionName, String createdByUserLoginId, String description){
        EduCourseSession eduCourseSession = new EduCourseSession();
        eduCourseSession.setCourseId(courseId);
        eduCourseSession.setSessionName(sessionName);
        eduCourseSession.setCreatedByUserLoginId(createdByUserLoginId);
        eduCourseSession.setDescription(description);
        eduCourseSession.setCreatedStamp(new Date());
        eduCourseSession.setLastUpdated(new Date());
        return eduCourseSessionRepo.save(eduCourseSession);
    }

}
