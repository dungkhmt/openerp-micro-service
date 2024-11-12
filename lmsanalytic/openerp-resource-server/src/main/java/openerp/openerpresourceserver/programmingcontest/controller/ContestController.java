package openerp.openerpresourceserver.programmingcontest.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.callexternalapi.service.ApiService;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.*;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.programmingcontest.service.LmsContestSubmissionService;
import openerp.openerpresourceserver.programmingcontest.service.ProgrammingContestProblemRankingService;
import openerp.openerpresourceserver.programmingcontest.service.ProgrammingContestRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private LmsContestSubmissionRepo lmsContestSubmissionRepo;
    private ApiService apiService;

    @PostMapping("/create-contest-submission")
    public ResponseEntity<?> createContestSubmission(Principal principal, @RequestBody ModelCreateContestSubmission m){
        //log.info("createContestSubmission, submissionId = " + m.getContestSubmissionId());

        lmsContestSubmissionService.save(m);

        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/get-submissions")
    public ResponseEntity<?> getAllContestSubmissions(Principal principal){
        List<LmsContestSubmission> submissions = lmsContestSubmissionService.findAll();
        log.info("getAllContestSubmissions, submissions = " + submissions.size());
        return ResponseEntity.ok().body(submissions);
    }

    @GetMapping("/get-submissions/{userId}")
    public ResponseEntity<?> getSubmissionsByStudent(@PathVariable String userId) {
        List<LmsContestSubmission> submissions = lmsContestSubmissionService.findSubmissionsByUserSubmissionId(userId);
        int numberOfSubmissions = submissions.size();
        int numberOfAcceptedSubmissions = (int) submissions.stream()
                .filter(submission -> "Accept".equalsIgnoreCase(submission.getStatus()))
                .count();
        int numberOfCompileErrorSubmissions = (int) submissions.stream()
                .filter(submission -> "Compile Error".equalsIgnoreCase(submission.getStatus()))
                .count();
        UserSubmissionsResponse response = new UserSubmissionsResponse(numberOfSubmissions, numberOfAcceptedSubmissions, numberOfCompileErrorSubmissions, submissions);
        log.info("Fetched {} submissions ({} accepted and {} compile error) for userId = {}", numberOfSubmissions, numberOfAcceptedSubmissions, numberOfCompileErrorSubmissions, userId);
        return ResponseEntity.ok(response);
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
        //Date toDate = new Date();
        //Date toDate = lmsContestSubmissionRepo.findMinSubmissionCreatedStamp();
        List<LmsContestSubmission> L = lmsContestSubmissionRepo.findEarlestPage5Items();
        if(L == null || L.size() == 0) return ResponseEntity.ok().body("EMPTY");
        for(LmsContestSubmission sub: L){
            log.info("synchronizeContestSubmission, among 5 items " + sub.getContestSubmissionId() + "," + sub.getUserSubmissionId() + ", time = " + sub.getSubmissionCreatedStamp());
        }
        Date toDate = L.get(0).getSubmissionCreatedStamp();
        log.info("synchronizeContestSubmission, toDate = {}",toDate);
        if(toDate == null) return ResponseEntity.ok().body("toDate NULL");
        //Date fromDate = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sFromDate = "2019-09-01 10:30:00";
        Date fromDate = null;
        try {
            fromDate = formatter.parse(sFromDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        //fromDate.setMonth(9);
        //fromDate.setYear(2024);
        //fromDate.setDate(1);
        m.setFromDate(fromDate);
        m.setToDate(toDate);
        ResponseEntity<?> res = apiService.callGetContestSubmissionPageOfPeriodAPI("https://hustack.soict.ai/api/get-contest-submissions-page-date-between/",m);
        //List<ContestSubmissionEntity> L = (List<ContestSubmissionEntity>)res.getBody();
        String body = res.getBody().toString();
        Gson gson = new Gson();
        //log.info("synchronizeContestSubmission, got body = " + body);
        ModelResponseGetContestSubmissionPage result = gson.fromJson(body,ModelResponseGetContestSubmissionPage.class);

        for( ContestSubmissionEntity s: result.getSubmissions()){
            LmsContestSubmission sub = new LmsContestSubmission();
            sub.setContestSubmissionId(s.getContestSubmissionId());
            sub.setContestId(s.getContestId());
            sub.setProblemId(s.getProblemId());
            sub.setUserSubmissionId(s.getUserId());
            sub.setPoint(s.getPoint());
            sub.setTestCasePass(s.getTestCasePass());
            sub.setSourceCode(s.getSourceCode());
            sub.setSourceCodeLanguage(s.getSourceCodeLanguage());
            sub.setStatus(s.getStatus());
            sub.setSubmissionCreatedStamp(s.getCreatedAt());
            sub.setSubmittedByUserId(s.getSubmittedByUserId());
            sub.setMemoryUsage(s.getMemoryUsage());
            sub.setRunTime(s.getRuntime());
            sub.setManagementStatus(s.getManagementStatus());
            sub.setViolateForbiddenInstructions(s.getViolateForbiddenInstruction());
            sub.setViolateForbiddenInstructionMessage(s.getViolateForbiddenInstructionMessage());
            sub.setMessage(s.getMessage());
            sub = lmsContestSubmissionRepo.save(sub);
            log.info("synchronizeContestSubmission, save submission " + s.getContestSubmissionId() + ", date = " + s.getCreatedAt() + " user " + s.getUserId() + " contest " + s.getContestId() + " problem " + s.getProblemId());

        }
        //log.info("synchronizeContestSubmission, got {}, toString = {}",res,res.toString());
        return ResponseEntity.ok().body(res);
    }
    public static void main(String[] args){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sFromDate = "2024-09-01 10:30:00";
        Date fromDate = null;
        try {
            fromDate = formatter.parse(sFromDate);
            System.out.println("format date = " + fromDate.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

