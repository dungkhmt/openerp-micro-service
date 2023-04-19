package com.hust.baseweb.applications.education.report.controller;

import com.hust.baseweb.applications.education.report.model.GetClassParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.StudentClassParticipationOutputModel;
import com.hust.baseweb.applications.education.report.service.StudentClassParticipationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@Log4j2
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ClassParticipationController {

    private StudentClassParticipationService studentClassParticipationService;

    @PostMapping("/get-class-participation-statistic")
    public ResponseEntity<?> getClassParticipationStatistic(
        Principal principal,
        GetClassParticipationStatisticInputModel input
    ) {
        List<StudentClassParticipationOutputModel> studentClassParticipationOutputModels =
            studentClassParticipationService.getStudentClassParticipationStatistic(input);

        return ResponseEntity.ok().body(studentClassParticipationOutputModels);
    }
}
