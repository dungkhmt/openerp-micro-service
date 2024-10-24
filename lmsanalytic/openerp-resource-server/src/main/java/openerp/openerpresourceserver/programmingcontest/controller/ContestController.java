package openerp.openerpresourceserver.programmingcontest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.model.ModelCreateContestSubmission;
import openerp.openerpresourceserver.programmingcontest.service.LmsContestSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ContestController {
    private LmsContestSubmissionService lmsContestSubmissionService;

    @PostMapping("/create-contest-submission")
    public ResponseEntity<?> createContestSubmission(Principal principal, @RequestBody ModelCreateContestSubmission m){
        log.info("createContestSubmission, submissionId = " + m.getContestSubmissionId());

        lmsContestSubmissionService.save(m);

        return ResponseEntity.ok().body("OK");
    }
}

