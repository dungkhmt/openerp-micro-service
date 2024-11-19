package openerp.openerpresourceserver.scheduledservice;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.callexternalapi.service.ApiService;
import openerp.openerpresourceserver.log.entity.LmsLog;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.programmingcontest.entity.CompositeProgrammingContestProblemRankingId;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.ContestSubmissionEntity;
import openerp.openerpresourceserver.programmingcontest.model.ModelInputGetContestSubmissionPage;
import openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetContestSubmissionPage;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestProblemRankingRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsContestSubmissionProcessor {
    public static final String TABLE_PROGRAMMING_CONTEST_PROBLEM_RANKING = "programming_contest_problem_ranking";
    public static final String TABLE_PROGRAMMING_CONTEST_RANKING = "programming_contest_ranking";
    public static final String TABLE_CONTEST_SUBMISSION = "lms_contest_submission";

    public static final String MODULE_CONTEST_PROBLEM_RANKING = "CONTEST_PROBLEM_RANKING";

    private static final Logger log = LoggerFactory.getLogger(LmsLogProcessor.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LmsContestSubmissionRepo lmsContestSubmissionRepo;
    private LastTimeProcessRepo lastTimeProcessRepo;
    private ProgrammingContestProblemRankingRepo programmingContestProblemRankingRepo;
    private ProgrammingContestRankingRepo programmingContestRankingRepo;

    private ApiService apiService;

    public static String composeKey(String userId, String contestId, String problemId){
        return userId + "$" + contestId + "$" + problemId;
    }
    public static String composeKey(String userId, String contestId){
        return userId + "$" + contestId;
    }

    @Scheduled(fixedRate = 6000)
    @Transactional
    public void processCountSubmissionsOfParticipants(){
        Date currentDate = new Date();
        log.info("processCountSubmissionsOfParticipants, run at time point {}",currentDate);
        // to be completed by huyentm


    }
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void process(){
        //log.info("LmsContestSubmissionProcessor -> run process time = {}",new Date());
        //LastTimeProcess ltp = lastTimeProcessRepo.findById(TABLE_CONTEST_SUBMISSION).orElse(null);
        LastTimeProcess ltp = lastTimeProcessRepo.findByTableNameAndModule(TABLE_CONTEST_SUBMISSION,MODULE_CONTEST_PROBLEM_RANKING);
        List<LmsContestSubmission> logs = null;
        Date currentDate = new Date();
        if(ltp == null){
            logs = lmsContestSubmissionRepo.findAll();
            log.info("process, last time process NULL, get number items lms_contest_submissions = " + logs.size());
            ltp = new LastTimeProcess();
            ltp.setTableName(TABLE_CONTEST_SUBMISSION);
            ltp.setModule(MODULE_CONTEST_PROBLEM_RANKING);
            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
            log.info("process, last time process NULL, save new record to DB");
        }else{
            logs = lmsContestSubmissionRepo.findAllByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            log.info("process, last time process = " + ltp.getLastTimeProcess() + ", get number items lms_contest_submissions = " + logs.size());

            ltp.setLastTimeProcess(currentDate);
            ltp = lastTimeProcessRepo.save(ltp);
        }

        //HashMap<CompositeProgrammingContestProblemRankingId, Long> mUserContestProblem2Point = new HashMap<>();
        HashSet<String> userContestChanged = new HashSet();
        for(LmsContestSubmission sub: logs){
            String userId = sub.getUserSubmissionId();
            String contestId = sub.getContestId();
            String problemId = sub.getProblemId();
            ProgrammingContestProblemRanking R = programmingContestProblemRankingRepo.findByUserIdAndContestIdAndProblemId(userId,contestId,problemId);
            //ProgrammingContestRanking pcr = programmingContestRankingRepo.findByUserIdAndContestId(userId,contestId);
            //List<ProgrammingContestProblemRanking> pcprs = programmingContestProblemRankingRepo.findByUserIdAndContestId(userId, contestId);
            String userContestKey = composeKey(userId, contestId);
            userContestChanged.add(userContestKey);

            if(R == null){
                R = new ProgrammingContestProblemRanking();
                R.setContestId(contestId);
                R.setUserId(userId);
                R.setProblemId(problemId);
                Long p = Long.valueOf(sub.getPoint());
                R.setPoint(p);
                R.setCreatedStamp(new Date());
                R.setLastUpdatedStamp(new Date());
                R = programmingContestProblemRankingRepo.saveAndFlush(R);
                //log.info("process, save new record point = " + R.getContestId() + " to DB");
            }else{
                if(R.getPoint() < sub.getPoint()){
                    R.setPoint(Long.valueOf(sub.getPoint()));
                    R.setLastUpdatedStamp(new Date());
                    R = programmingContestProblemRankingRepo.saveAndFlush(R);
                    //log.info("process update with new record point " + R.getPoint());
                }
            }
            /*
            if(pcr == null){
                pcr = new ProgrammingContestRanking();
                pcr.setUserId(userId); pcr.setContestId(contestId);
                pcr.setPoint(Long.valueOf(sub.getPoint()));
                pcr = programmingContestRankingRepo.save(pcr);
            }else{

            }

             */
        }
        //log.info("process, aggregate userContestChanged = " + userContestChanged.size());

        // update to programming_contest_ranking
        for(String k: userContestChanged){
            //log.info("process, aggregate key = " + k);
            String[] s = k.split("\\$");
            String userId = s[0];
            String contestId = s[1];
            List<ProgrammingContestProblemRanking> L = programmingContestProblemRankingRepo
                    .findByUserIdAndContestId(userId, contestId);
           // log.info("process, aggregate with userId " + userId + " contestId " + contestId + " L.sz = " + L.size());
            long point = 0;
            for(ProgrammingContestProblemRanking pcpr: L){
                point += pcpr.getPoint();
            }


            ProgrammingContestRanking pcr = programmingContestRankingRepo.findByUserIdAndContestId(userId,contestId);
           // log.info("process, aggregate with userId " + userId + " contestId " + contestId + " L.sz = " + L.size() + " point = " + point + " record pcr = {}",pcr);

            if(pcr == null){
                pcr = new ProgrammingContestRanking();
                pcr.setUserId(userId); pcr.setContestId(contestId);
                pcr.setPoint(Long.valueOf(point));
                pcr.setCreatedStamp(new Date()); pcr.setLastUpdatedStamp(new Date());
                pcr = programmingContestRankingRepo.saveAndFlush(pcr);
            }else{
                pcr.setPoint(Long.valueOf(point));
                pcr.setLastUpdatedStamp(new Date());
                pcr = programmingContestRankingRepo.save(pcr);
            }
        }
    }
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processMigrateContestSubmissions(){
        Date currentDate = new Date();
        //log.info("processMigrateContestSubmissions, run at time point {}",currentDate);
        ModelInputGetContestSubmissionPage m = new ModelInputGetContestSubmissionPage();
        m.setLimit(20);
        m.setOffset(0);
        Date toDate = new Date();
        //Date toDate = lmsContestSubmissionRepo.findMinSubmissionCreatedStamp();
        List<LmsContestSubmission> L = lmsContestSubmissionRepo.findEarlestPage5Items();
        if(L == null || L.size() == 0){
            //return ResponseEntity.ok().body("EMPTY");
        }else {
            for (LmsContestSubmission sub : L) {
                //log.info("processMigrateContestSubmissions, among 5 items " + sub.getContestSubmissionId() + "," + sub.getUserSubmissionId() + ", time = " + sub.getSubmissionCreatedStamp());
            }
            if(L.get(0).getSubmissionCreatedStamp() != null)
                toDate = L.get(0).getSubmissionCreatedStamp();
            //log.info("processMigrateContestSubmissions, toDate = {}", toDate);
            if (toDate == null) {
                toDate = new Date();
                //return ResponseEntity.ok().body("toDate NULL");
            }
        }
        //Date fromDate = new Date();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sFromDate = "2010-09-01 10:30:00";
        Date fromDate = null;
        try {
            fromDate = formatter.parse(sFromDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("processMigrateContestSubmissions, run at time point {}",currentDate);
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

        int cnt = 0;
        for( ContestSubmissionEntity s: result.getSubmissions()){
            //log.info("processMigrateContestSubmissions, GOT sub id = " + s.getContestSubmissionId() + " time = " + s.getCreatedAt());
            LmsContestSubmission tmp = lmsContestSubmissionRepo.findByContestSubmissionId(s.getContestSubmissionId());
            if(tmp != null){
                log.info("processMigrateContestSubmissions, submissionId = " + s.getContestSubmissionId() + " exists -> continue");
                continue;
            }
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
            //log.info("processMigrateContestSubmissions, save submission " + s.getContestSubmissionId() + ", date = " + s.getCreatedAt() + " user " + s.getUserId() + " contest " + s.getContestId() + " problem " + s.getProblemId());
            cnt ++;
        }

        log.info("processMigrateContestSubmissions DONE with " + cnt + " items migrated");

    }


    public static void main(String[] args){
        String key = "dungpq$123456";
        String[] s = key.split("\\$");
        System.out.println(s[0]);
        System.out.println(s[1]);
    }
}
