package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.model.edutestquizparticipation.EduTestQuizParticipationCreateInputModel;
import com.hust.baseweb.applications.education.quiztest.service.EduTestQuizParticipantService;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class EduTestQuizParticipantController {

    private EduTestQuizParticipantService eduTestQuizParticipantService;
    private UserService userService;

    @PostMapping("/create-quiz-test-participation-register")
    public ResponseEntity<?> createQuizTestParticipationRegister(
        Principal principal, @RequestBody
        EduTestQuizParticipationCreateInputModel input
    ) {
        UserLogin u = userService.findById(principal.getName());
        log.info("createQuizTestParticipationRegister, userLoginId = " + u.getUserLoginId());
        EduTestQuizParticipant eduTestQuizParticipant = eduTestQuizParticipantService.register(u, input);
        return ResponseEntity.ok().body(eduTestQuizParticipant);
    }
}
