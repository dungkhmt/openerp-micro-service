package openerp.openerpresourceserver.programmingcontest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.model.ModelCreateContestSubmission;
import openerp.openerpresourceserver.programmingcontest.service.LmsContestSubmissionService;
import openerp.openerpresourceserver.programmingcontest.service.ProgrammingContestProblemRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ContestController {
    private LmsContestSubmissionService lmsContestSubmissionService;
    private ProgrammingContestProblemRankingService programmingContestProblemRankingService;
    @PostMapping("/create-contest-submission")
    public ResponseEntity<?> createContestSubmission(Principal principal, @RequestBody ModelCreateContestSubmission m){
        log.info("createContestSubmission, submissionId = " + m.getContestSubmissionId());

        lmsContestSubmissionService.save(m);

        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/get-contest-problem-ranking")
    public ResponseEntity<?> getContestProblemRanking(Principal principal){
        List<ProgrammingContestProblemRanking> res = programmingContestProblemRankingService.findAll();
        return ResponseEntity.ok().body(res);
    }
}

