package com.hust.baseweb.applications.education.report.controller;

import com.hust.baseweb.applications.education.report.model.quizparticipation.GetQuizParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.quizparticipation.QuizParticipationStatisticOutputModel;
import com.hust.baseweb.applications.education.report.service.quizparticipation.QuizParticipationStatisticService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizParticipationController {

    private QuizParticipationStatisticService quizParticipationStatisticService;

    @PostMapping("/get-quiz-participation-statistic")
    public ResponseEntity<?> getQuizParticipationStatistic(
        Principal principal,
        @RequestBody GetQuizParticipationStatisticInputModel input
    ) {
        List<QuizParticipationStatisticOutputModel> quizParticipationStatisticOutputModelList =
            quizParticipationStatisticService.getQuizParticipationStatistic(input);

        return ResponseEntity.ok().body(quizParticipationStatisticOutputModelList);
    }
}
