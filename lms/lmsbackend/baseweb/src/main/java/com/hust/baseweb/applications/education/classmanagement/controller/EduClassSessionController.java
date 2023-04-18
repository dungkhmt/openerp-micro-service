package com.hust.baseweb.applications.education.classmanagement.controller;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.model.CreateEduClassSessionIM;
import com.hust.baseweb.applications.education.classmanagement.model.CreateQuizTestOfClassSessionIM;
import com.hust.baseweb.applications.education.classmanagement.model.EduClassSessionDetailOM;
import com.hust.baseweb.applications.education.classmanagement.service.EduClassSessionService;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@RequestMapping("/edu/class")
@AllArgsConstructor(onConstructor = @__(@Autowired))

public class EduClassSessionController {
    private EduClassSessionService eduClassSessionService;
    private UserService userService;

    @GetMapping("/get-sessions-of-class/{classId}")
    public ResponseEntity<?> getSessionsOfClass(Principal principal, @PathVariable UUID classId){

        List<EduClassSession> lst = eduClassSessionService.findAllByClassId(classId);
        return ResponseEntity.ok().body(lst);
    }
    @GetMapping("/get-session-detail/{sessionId}")
    public ResponseEntity<?> getSessionDetail(Principal principal, @PathVariable UUID sessionId){
        log.info("getSessionDetail sessionId = " + sessionId);
        EduClassSessionDetailOM sessionDetail = eduClassSessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok().body(sessionDetail);
    }

    @PostMapping("/add-a-session-of-class")
    public ResponseEntity<?> addASessionOfClass(Principal principal, @RequestBody CreateEduClassSessionIM input){
        UserLogin u = userService.findById(principal.getName());
        log.info("addASessionOfClass, sessionName = " + input.getSessionName());
        EduClassSession o = eduClassSessionService.save(input.getClassId(),input.getSessionName(), input.getDescription(),u.getUserLoginId());
        return ResponseEntity.ok().body(o);
    }
    @GetMapping("/get-quiz-test-list-of-session/{sessionId}")
    public ResponseEntity<?> getQuizTestOfSession(Principal principal, @PathVariable UUID sessionId){
        List<EduQuizTest> lst = eduClassSessionService.findAllBySession(sessionId);
        return ResponseEntity.ok().body(lst);
    }
    @PostMapping("/add-a-quiz-test-of-class-session")
    public ResponseEntity<?> addQuizTestOfClassSession(Principal principal, @RequestBody CreateQuizTestOfClassSessionIM input){
        log.info("addQuizTestOfClassSession....testName = " + input.getTestName());
        EduQuizTest eduQuizTest = eduClassSessionService.createQuizTestOfClassSession(input.getSessionId(),  input.getTestId(), input.getTestName(), input.getDuration());
        return ResponseEntity.ok().body(eduQuizTest);

    }
}
