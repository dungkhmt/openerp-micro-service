package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.EduCourseSession;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduCourseSessionInteractiveQuizServiceImpl implements EduCourseSessionInteractiveQuizService{
    private EduCourseSessionInteractiveQuizRepo eduCourseSessionInteractiveQuizRepo;
    @Override
    public EduCourseSessionInteractiveQuiz createCourseSessionInteractiveQuiz(String interactiveQuizName, UUID sessionId, String description){
        EduCourseSessionInteractiveQuiz eduCourseSessionInteractiveQuiz = new EduCourseSessionInteractiveQuiz();
        eduCourseSessionInteractiveQuiz.setInteractiveQuizName(interactiveQuizName);
        eduCourseSessionInteractiveQuiz.setSessionId(sessionId);
        eduCourseSessionInteractiveQuiz.setDescription(description);
        eduCourseSessionInteractiveQuiz.setCreatedStamp(new Date());
        eduCourseSessionInteractiveQuiz.setLastUpdated(new Date());
        return eduCourseSessionInteractiveQuizRepo.save(eduCourseSessionInteractiveQuiz);
    }
}
