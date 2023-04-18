package com.hust.baseweb.applications.education.quiztest.controller;

import com.hust.baseweb.applications.education.quiztest.entity.QuizGroupQuestionAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.AddQuizGroupQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.QuizGroupQuestionDetailOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.RemoveQuizGroupQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.service.QuizGroupQuestionAssignmentService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Log4j2
@Controller
@Validated
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin

public class QuizGroupQuestionAssignmentController {

    private QuizGroupQuestionAssignmentService quizGroupQuestionAssignmentService;

    @GetMapping("/get-quiz-group-question-assignment-of-test/{testId}")
    public ResponseEntity<?> getQuizGroupQuestionAssignments(Principal principal, @PathVariable String testId) {
        List<QuizGroupQuestionDetailOutputModel> quizGroupQuestionAssignments =
            quizGroupQuestionAssignmentService.findAllQuizGroupQuestionAssignmentOfTest(testId);

        return ResponseEntity.ok().body(quizGroupQuestionAssignments);
    }

    @PostMapping("/remove-quizgroup-question-assignment")
    public ResponseEntity<?> removeQuizGroupQuestionAssignment(
        Principal principal, @RequestBody
        RemoveQuizGroupQuestionInputModel input
    ) {
        quizGroupQuestionAssignmentService.removeQuizGroupQuestionAssignment(input);
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/add-quizgroup-question-assignment")
    public ResponseEntity<?> addQuizGroupQuestionAssignment(
        @RequestBody AddQuizGroupQuestionInputModel input
    ) {
        QuizGroupQuestionAssignment gq = quizGroupQuestionAssignmentService.addQuizGroupQuestionAssignment(input);
        return ResponseEntity.ok().body(gq);
    }
}
