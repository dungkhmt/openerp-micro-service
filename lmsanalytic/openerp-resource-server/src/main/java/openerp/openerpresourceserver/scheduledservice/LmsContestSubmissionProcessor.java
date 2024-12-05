package openerp.openerpresourceserver.scheduledservice;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.callexternalapi.service.ApiService;
import openerp.openerpresourceserver.entity.LmsanalyticSystemParams;
import openerp.openerpresourceserver.masterconfig.entity.LastTimeProcess;
import openerp.openerpresourceserver.masterconfig.repo.LastTimeProcessRepo;
import openerp.openerpresourceserver.programmingcontest.entity.LmsContestSubmission;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestProblemRanking;
import openerp.openerpresourceserver.programmingcontest.entity.ProgrammingContestRanking;
import openerp.openerpresourceserver.programmingcontest.model.ContestSubmissionEntity;
import openerp.openerpresourceserver.programmingcontest.model.ModelInputGetContestSubmissionPage;
import openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetContestSubmissionPage;
import openerp.openerpresourceserver.programmingcontest.model.ModelResponseGetSubmissionsWithStatus;
import openerp.openerpresourceserver.programmingcontest.repo.LmsContestSubmissionRepo;
import openerp.openerpresourceserver.repo.LmsanalyticSystemParamsRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestProblemRankingRepo;
import openerp.openerpresourceserver.programmingcontest.repo.ProgrammingContestRankingRepo;
import openerp.openerpresourceserver.userfeaturestore.entity.UserFeatures;
import openerp.openerpresourceserver.userfeaturestore.repo.UserFeaturesRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LmsContestSubmissionProcessor {
    public static final String TABLE_PROGRAMMING_CONTEST_PROBLEM_RANKING = "programming_contest_problem_ranking";
    public static final String TABLE_PROGRAMMING_CONTEST_RANKING = "programming_contest_ranking";
    public static final String TABLE_CONTEST_SUBMISSION = "lms_contest_submission";

    public static final String MODULE_CONTEST_PROBLEM_RANKING = "CONTEST_PROBLEM_RANKING";
    public static final String MODULE_CONTEST_PROBLEM_COUNT_SUBMISSIONS = "CONTEST_COUNT_SUBMISSIONS";


    private static final Logger log = LoggerFactory.getLogger(LmsLogProcessor.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private LmsContestSubmissionRepo lmsContestSubmissionRepo;
    private LastTimeProcessRepo lastTimeProcessRepo;
    private ProgrammingContestProblemRankingRepo programmingContestProblemRankingRepo;
    private ProgrammingContestRankingRepo programmingContestRankingRepo;
    private UserFeaturesRepo userFeaturesRepo;

    private ApiService apiService;
    private LmsanalyticSystemParamsRepo lmsanalyticSystemParamsRepo;

    public static String composeKey(String userId, String contestId, String problemId){
        return userId + "$" + contestId + "$" + problemId;
    }
    public static String composeKey(String userId, String contestId){
        return userId + "$" + contestId;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processCountSubmissionsOfParticipants(){
        Date currentDate = new Date();
        log.info("processCountSubmissionsOfParticipants, run at time point {}",currentDate);
        // to be completed by huyentm
        LastTimeProcess ltp = lastTimeProcessRepo.findByTableNameAndModule(TABLE_CONTEST_SUBMISSION, MODULE_CONTEST_PROBLEM_COUNT_SUBMISSIONS);
        //List<LmsContestSubmission> newSubmissions;
        List<ModelResponseGetSubmissionsWithStatus> newSubmissions;

        if (ltp == null) {
            //newSubmissions = lmsContestSubmissionRepo.findAll();
            newSubmissions = lmsContestSubmissionRepo.findAllSubmissionWithStatus();
            log.info("processCountSubmissionsOfParticipants, last time process NULL, get number items lms_contest_submissions = " + newSubmissions.size());
            ltp = new LastTimeProcess();
            ltp.setTableName(TABLE_CONTEST_SUBMISSION);
            ltp.setModule(MODULE_CONTEST_PROBLEM_COUNT_SUBMISSIONS);
            ltp.setLastTimeProcess(currentDate);
            lastTimeProcessRepo.save(ltp);
            log.info("processCountSubmissionsOfParticipants, last time process NULL, save new record to DB");
        } else {
            //newSubmissions = lmsContestSubmissionRepo.findAllByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            newSubmissions = lmsContestSubmissionRepo.findAllWithStatusByCreatedStampBetween(ltp.getLastTimeProcess(), currentDate);
            log.info("processCountSubmissionsOfParticipants, last time process = " + ltp.getLastTimeProcess() + ", get number items lms_contest_submissions = " + newSubmissions.size());
            ltp.setLastTimeProcess(currentDate);
            lastTimeProcessRepo.save(ltp);
        }

        HashMap<String, Integer> successSubmissionCount = new HashMap<>();
        HashMap<String, Integer> compileFailSubmissionCount = new HashMap<>();
        HashMap<String, Integer> partialSubmissionCount = new HashMap<>();
        HashMap<String, Integer> failedSubmissionCount = new HashMap<>();
        HashMap<String, Integer> totalSubmissionCount = new HashMap<>();

        Set<String> userIds = new HashSet<>();

        //for (LmsContestSubmission submission : newSubmissions) {
        for(ModelResponseGetSubmissionsWithStatus submission: newSubmissions){
            String userId = submission.getUserSubmissionId();
            userIds.add(userId);
            if (ContestSubmissionEntity.SUBMISSION_STATUS_ACCEPTED.equals(submission.getStatus())) {
                successSubmissionCount.put(userId, successSubmissionCount.getOrDefault(userId, 0) + 1);
            }else if (ContestSubmissionEntity.SUBMISSION_STATUS_FAILED.equals(submission.getStatus())) {
                failedSubmissionCount.put(userId, successSubmissionCount.getOrDefault(userId, 0) + 1);
            }else if (ContestSubmissionEntity.SUBMISSION_STATUS_PARTIAL.equals(submission.getStatus())) {
                partialSubmissionCount.put(userId, successSubmissionCount.getOrDefault(userId, 0) + 1);
            }else if (ContestSubmissionEntity.SUBMISSION_STATUS_COMPILE_ERROR.equals(submission.getStatus())) {
                compileFailSubmissionCount.put(userId, compileFailSubmissionCount.getOrDefault(userId, 0) + 1);
            }
        }
        for(String userId: userIds) {
            int c1 = successSubmissionCount.getOrDefault(userId, 0);
            int c2 = failedSubmissionCount.getOrDefault(userId,0);
            int c3 = partialSubmissionCount.getOrDefault(userId,0);
            int c4 = compileFailSubmissionCount.getOrDefault(userId,0);
            totalSubmissionCount.put(userId,c1+c2+c3+c4);
            log.info("processCountSubmissionsOfParticipants, user " + userId + ": c1 = " + c1 + " c2 = " + c2 + " c3 = " + c3 + " c4 = " + c4 + " total = " + totalSubmissionCount.get(userId));
        }

        // update or create new to DB
        for(String userId: userIds) {
            UserFeatures uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_CONTEST_SUBMISSIONS);
            if(uf == null){
                uf = UserFeatures.builder()
                        .userId(userId)
                        .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_SUBMISSIONS)
                        .value(totalSubmissionCount.getOrDefault(userId,0))
                        .status("ACTIVE")
                        .lastUpdatedStamp(currentDate)
                        .createdStamp(currentDate)
                        .build();
            }else{
                uf.setValue(uf.getValue() + totalSubmissionCount.getOrDefault(userId,0));
            }
            log.info("processCountSubmissionsOfParticipants, start save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());
            uf = userFeaturesRepo.save(uf);
            log.info("processCountSubmissionsOfParticipants, finished save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());

            // process update accept submissions
            uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_CONTEST_ACCEPT_SUBMISSIONS);
            if(uf == null){
                uf = UserFeatures.builder()
                        .userId(userId)
                        .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_ACCEPT_SUBMISSIONS)
                        .value(totalSubmissionCount.getOrDefault(userId,0))
                        .status("ACTIVE")
                        .lastUpdatedStamp(currentDate)
                        .createdStamp(currentDate)
                        .build();
            }else{
                uf.setValue(uf.getValue() + successSubmissionCount.getOrDefault(userId,0));
            }
            log.info("processCountSubmissionsOfParticipants, start save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());
            uf = userFeaturesRepo.save(uf);
            log.info("processCountSubmissionsOfParticipants, finished save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());

            // process update compile error submissions
            uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_CONTEST_COMPILE_ERROR_SUBMISSIONS);
            if(uf == null){
                uf = UserFeatures.builder()
                        .userId(userId)
                        .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_COMPILE_ERROR_SUBMISSIONS)
                        .value(totalSubmissionCount.getOrDefault(userId,0))
                        .status("ACTIVE")
                        .lastUpdatedStamp(currentDate)
                        .createdStamp(currentDate)
                        .build();
            }else{
                uf.setValue(uf.getValue() + compileFailSubmissionCount.getOrDefault(userId,0));
            }
            log.info("processCountSubmissionsOfParticipants, start save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());
            uf = userFeaturesRepo.save(uf);
            log.info("processCountSubmissionsOfParticipants, finished save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());

            // process update partial submission
            uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_CONTEST_PARTIAL_SUBMISSIONS);
            if(uf == null){
                uf = UserFeatures.builder()
                        .userId(userId)
                        .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_PARTIAL_SUBMISSIONS)
                        .value(totalSubmissionCount.getOrDefault(userId,0))
                        .status("ACTIVE")
                        .lastUpdatedStamp(currentDate)
                        .createdStamp(currentDate)
                        .build();
            }else{
                uf.setValue(uf.getValue() + partialSubmissionCount.getOrDefault(userId,0));
            }
            log.info("processCountSubmissionsOfParticipants, start save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());
            uf = userFeaturesRepo.save(uf);
            log.info("processCountSubmissionsOfParticipants, finished save " + uf.getUserId() + ", " + uf.getFeatureId() + ", " + uf.getValue());

            // process failed submissions
            uf = userFeaturesRepo.findByUserIdAndFeatureId(userId,UserFeatures.FEATURE_NUMBER_CONTEST_FAILED_SUBMISSIONS);
            if(uf == null){
                uf = UserFeatures.builder()
                        .userId(userId)
                        .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_FAILED_SUBMISSIONS)
                        .value(totalSubmissionCount.getOrDefault(userId,0))
                        .status("ACTIVE")
                        .lastUpdatedStamp(currentDate)
                        .createdStamp(currentDate)
                        .build();
            }else{
                uf.setValue(uf.getValue() + failedSubmissionCount.getOrDefault(userId,0));
            }
            uf = userFeaturesRepo.save(uf);

        }
        /*
        for (LmsContestSubmission submission : newSubmissions) {
            String userId = submission.getUserSubmissionId();

            int totalSubmission = successSubmissionCount.getOrDefault(userId, 0) + compileFailSubmissionCount.getOrDefault(userId, 0);
            int successSubmission = successSubmissionCount.getOrDefault(userId, 0);
            int failedSubmission = compileFailSubmissionCount.getOrDefault(userId, 0);

            UserFeatures totalSubmissionFeature = UserFeatures.builder()
                    .userId(userId)
                    .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_SUBMISSIONS)
                    .value(totalSubmission)
                    .status("ACTIVE")
                    .lastUpdatedStamp(currentDate)
                    .createdStamp(currentDate)
                    .build();

            UserFeatures successSubmissionFeature = UserFeatures.builder()
                    .userId(userId)
                    .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_ACCEPT_SUBMISSIONS)
                    .value(successSubmission)
                    .status("ACTIVE")
                    .lastUpdatedStamp(currentDate)
                    .createdStamp(currentDate)
                    .build();

            UserFeatures failedSubmissionFeature = UserFeatures.builder()
                    .userId(userId)
                    .featureId(UserFeatures.FEATURE_NUMBER_CONTEST_COMPILE_ERROR_SUBMISSIONS)
                    .value(failedSubmission)
                    .status("ACTIVE")
                    .lastUpdatedStamp(currentDate)
                    .createdStamp(currentDate)
                    .build();

            userFeaturesRepo.save(totalSubmissionFeature);
            userFeaturesRepo.save(successSubmissionFeature);
            userFeaturesRepo.save(failedSubmissionFeature);

         */

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
    //@Scheduled(fixedRate = 5000)
    @Transactional
    public void processMigrateContestSubmissions(){
        Date currentDate = new Date();
        log.info("processMigrateContestSubmissions, START run at time point {}",currentDate);
        //if(true) return;
        ModelInputGetContestSubmissionPage m = new ModelInputGetContestSubmissionPage();
        List<LmsanalyticSystemParams> params = lmsanalyticSystemParamsRepo.findAllByParam(LmsanalyticSystemParams.MIGRATE_CONTEST_SUBMISSION_NUMBER_ITEMS_PER_QUERY);
        log.info("processMigrateContestSubmissions, params,size = " + params.size());
        int length = 20;
        if(params != null && params.size() > 0){
            try {
                length = Integer.valueOf(params.get(0).getValue());
            }catch (Exception e){
                log.info("processMigrateContestSubmissions HAS EXCEPTION, params.size = " + params.size());
                e.printStackTrace();
            }
        }
        log.info("processMigrateContestSubmissions, GOT LENGTH FROM DB = " + length);
        m.setLimit(length);
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
