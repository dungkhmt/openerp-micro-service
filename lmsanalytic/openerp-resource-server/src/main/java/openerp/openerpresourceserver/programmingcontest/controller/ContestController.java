package openerp.openerpresourceserver.programmingcontest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.callexternalapi.service.ApiService;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.*;
import openerp.openerpresourceserver.programmingcontest.service.LmsContestSubmissionService;
import openerp.openerpresourceserver.programmingcontest.service.ProgrammingContestProblemRankingService;
import openerp.openerpresourceserver.programmingcontest.service.ProgrammingContestRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ContestController {
    private LmsContestSubmissionService lmsContestSubmissionService;
    private ProgrammingContestProblemRankingService programmingContestProblemRankingService;
    private ProgrammingContestRankingService programmingContestRankingService;

    private ApiService apiService;

    @PostMapping("/create-contest-submission")
    public ResponseEntity<?> createContestSubmission(Principal principal, @RequestBody ModelCreateContestSubmission m){
        //log.info("createContestSubmission, submissionId = " + m.getContestSubmissionId());

        lmsContestSubmissionService.save(m);

        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/get-contest-problem-ranking")
    public ResponseEntity<?> getContestProblemRanking(Principal principal){
        List<ProgrammingContestProblemRanking> res = programmingContestProblemRankingService.findAll();
        log.info("getContestProblemRanking, res = " + res.size());
        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/get-contest-ranking")
    public ResponseEntity<?> getContestRanking(Principal principal){
        List<ProgrammingContestRanking> res = programmingContestRankingService.findAll();
        //log.info("getContestRanking at {}", new Date());
        return ResponseEntity.ok().body(res);
    }
    @GetMapping("/get-contest-ranking/{contestId}")
    public ResponseEntity<?> getContestRankingByContestId(Principal principal, @PathVariable String contestId){
        List<ProgrammingContestRanking> res = programmingContestRankingService
                .findAllByContestId(contestId);
        //log.info("getContestRanking at {}", new Date());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/get-contestids")
    public ResponseEntity<?> getContestIds(Principal principal){
        List<ContestModelRepsonse> res = programmingContestRankingService.getContestAllIds();
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/synchronize-contest-submission")
    public ResponseEntity<?> synchronizeContestSubmission(Principal principal){
        //List<ContestSubmissionEntity> sub = new ArrayList<ContestSubmissionEntity>();
        ModelInputGetContestSubmissionPage m = new ModelInputGetContestSubmissionPage();
        m.setLimit(10);
        m.setOffset(0);
        Date toDate = new Date();
        Date fromDate = new Date();
        fromDate.setMonth(9);
        fromDate.setYear(2024);
        fromDate.setDate(1);
        m.setFromDate(fromDate);
        m.setToDate(toDate);
        ResponseEntity<?> res = apiService.callGetContestSubmissionPageOfPeriodAPI("https://hustack.soict.ai/api/get-contest-submissions-page-date-between/",m);

        log.info("synchronizeContestSubmission, got {}, toString = {}",res,res.toString());
        return ResponseEntity.ok().body(res);
    }
}

