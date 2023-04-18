package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.model.EduQuizTestModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestquestion.CreateQuizTestQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.service.EduQuizTestQuizQuestionService;
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
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin
public class EduQuizTestQuizQuestionController {
    private EduQuizTestQuizQuestionService eduQuizTestQuizQuestionService;
    private UserService userService;

    @GetMapping("/get-quiz-test-using-question/{questionId}")
    public ResponseEntity<?> getQuizTestsUsingQuestion(Principal principal, @PathVariable UUID questionId){
        List<EduQuizTestModel> eduQuizTestModels = eduQuizTestQuizQuestionService.getQuizTestsUsingQuestion(questionId);
        return ResponseEntity.ok().body(eduQuizTestModels);
    }
    @GetMapping("/get-questions-of-quiz-test/{testId}")
    public ResponseEntity<?> getQuestionOfQuizTest(Principal principal, @PathVariable String testId){
        log.info("getQuestionOfQuizTest, testId = " + testId);
        List<QuizQuestionDetailModel> eduQuizTestQuizQuestionList =
            eduQuizTestQuizQuestionService.findAllByTestId(testId);
        return ResponseEntity.ok().body(eduQuizTestQuizQuestionList);
    }

    @PostMapping("/add-question-to-quiz-test")
    public ResponseEntity<?> addQuestionToQuizTest(Principal principal, @RequestBody CreateQuizTestQuestionInputModel input){
        UserLogin u = userService.findById(principal.getName());
        log.info("addQuestionToQuizTest, questionId = " + input.getQuestionId() + ", testId = " + input.getTestId());

        EduQuizTestQuizQuestion eduQuizTestQuizQuestion = eduQuizTestQuizQuestionService
            .createQuizTestQuestion(u,input);
        return ResponseEntity.ok().body(eduQuizTestQuizQuestion);
    }
    @PostMapping("/remove-question-from-quiz-test")
    public ResponseEntity<?> removeQuestionFromQuizTest(Principal principal, @RequestBody CreateQuizTestQuestionInputModel input){
        UserLogin u = userService.findById(principal.getName());
        log.info("removeQuestionFromQuizTest, questionId = " + input.getQuestionId() + ", testId = " + input.getTestId());

        EduQuizTestQuizQuestion eduQuizTestQuizQuestion = eduQuizTestQuizQuestionService
            .removeQuizTestQuestion(u,input);
        return ResponseEntity.ok().body(eduQuizTestQuizQuestion);
    }

}
