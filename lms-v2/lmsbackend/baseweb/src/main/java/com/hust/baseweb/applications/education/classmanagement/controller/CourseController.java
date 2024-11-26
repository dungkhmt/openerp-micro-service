package com.hust.baseweb.applications.education.classmanagement.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.EduCourseSession;
import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuiz;
import com.hust.baseweb.applications.education.model.AddCourseModel;
import com.hust.baseweb.applications.education.model.CourseSessionInteractiveQuizCreateModel;
import com.hust.baseweb.applications.education.model.EduCourseSessionModelCreate;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizRepo;
import com.hust.baseweb.applications.education.repo.EduCourseSessionRepo;
import com.hust.baseweb.applications.education.service.CourseService;
import com.hust.baseweb.applications.education.service.EduCourseSessionInteractiveQuizService;
import com.hust.baseweb.applications.education.service.EduCourseSessionService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Log4j2
@Controller
@Validated
@RequestMapping("/edu/course")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CourseController {
    private CourseService courseService;
    private EduCourseSessionRepo eduCourseSessionRepo;
    private EduCourseSessionService eduCourseSessionService;
    private EduCourseSessionInteractiveQuizService eduCourseSessionInteractiveQuizService;
    private EduCourseSessionInteractiveQuizRepo eduCourseSessionInteractiveQuizRepo;

    @PostMapping("/create")
    public ResponseEntity<?> addEduClass(Principal principal, @RequestBody AddCourseModel addCourseModel) {
        log.info("addEduClass, start....");
        EduCourse eduCourse = courseService.createCourse(addCourseModel.getCourseId(), addCourseModel.getCourseName(), addCourseModel.getCredit());
        if (eduCourse != null) {
            return ResponseEntity.ok().body(eduCourse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create-course-session")
    public ResponseEntity<?> createSessionOfCourse(
        Principal principal,
        @RequestBody EduCourseSessionModelCreate eduCourseSessionModelCreate
    ) {
        EduCourseSession eduCourseSession = eduCourseSessionService.createCourseSession(eduCourseSessionModelCreate.getCourseId(), eduCourseSessionModelCreate.getSessionName(), principal.getName(), eduCourseSessionModelCreate.getDescription());
        return ResponseEntity.ok().body(eduCourseSession);
    }

    @Secured("ROLE_TEACHER")
    @GetMapping("/get-course-sessions/{courseId}")
    public ResponseEntity<?> getSessionOfCourse(Principal principal, @PathVariable String courseId){
        List<EduCourseSession> eduCourseSessions = eduCourseSessionRepo.findByCourseId(courseId);
        return ResponseEntity.ok().body(eduCourseSessions);
    }

    @Secured("ROLE_TEACHER")
    @PostMapping("/create-course-session-interactive-quiz")
    public ResponseEntity<?> createCourseSessionInteractiveQuiz(
        Principal principal,
        @RequestBody CourseSessionInteractiveQuizCreateModel courseSessionInteractiveQuizCreateModel
    ) {
        return ResponseEntity.ok().body(eduCourseSessionInteractiveQuizService.createCourseSessionInteractiveQuiz(courseSessionInteractiveQuizCreateModel.getInteractiveQuizName(), courseSessionInteractiveQuizCreateModel.getSessionId(), courseSessionInteractiveQuizCreateModel.getDescription()));
    }
    
    @Secured("ROLE_TEACHER")
    @GetMapping("/get-interactive-quiz-of-course-session/{sessionId}")
    public ResponseEntity<?> getInteractiveQuizOfCourseSession(Principal principal, @PathVariable UUID sessionId){
        List<EduCourseSessionInteractiveQuiz> eduCourseSessionInteractiveQuizs = eduCourseSessionInteractiveQuizRepo.findBySessionId(sessionId);
        return ResponseEntity.ok().body(eduCourseSessionInteractiveQuizs);
    }

}
